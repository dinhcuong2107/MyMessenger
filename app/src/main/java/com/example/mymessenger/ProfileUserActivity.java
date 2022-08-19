package com.example.mymessenger;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymessenger.adapter.UsersAdapter;
import com.example.mymessenger.obj.ChatRoom;
import com.example.mymessenger.obj.Friends;
import com.example.mymessenger.obj.Messenger;
import com.example.mymessenger.obj.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ProfileUserActivity extends AppCompatActivity {
    String key_user="",key_friend="",time_now="",time_send="",day_send="",idchatzoom="";
    ImageView imageView;
    int status=2107;
    ProgressDialog progressDialog;
    TextView fullname,dob,caption,stt_friend,next_mess;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);

        imageView = (ImageView) findViewById(R.id.profile_avatar);
        fullname = (TextView) findViewById(R.id.profile_full_name);
        dob = (TextView) findViewById(R.id.profile_dob);
        caption = (TextView) findViewById(R.id.caption);

        stt_friend = (TextView) findViewById(R.id.profile_stt_friends);
        next_mess = (TextView) findViewById(R.id.profile_next_mess);
        next_mess.setVisibility(View.GONE);
        // nhận data từ intent
        key_user =  getIntent().getStringExtra("key_user");
        key_friend =  getIntent().getStringExtra("key_friend");

        databaseReference= FirebaseDatabase.getInstance().getReference("Users").child(key_friend);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);
                if (users.avatar.length() != 0)
                {
                    Picasso.with(ProfileUserActivity.this).load(users.avatar).into(imageView);
                }
                fullname.setText(""+users.fullname);
                dob.setText(""+users.dateofbirth);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("Friends");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (final DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Friends friends = dataSnapshot.getValue(Friends.class);
                    if (friends.id_user0.equals(key_user))
                    {
                        if (friends.id_user1.equals(key_friend))
                        {
                            idchatzoom = dataSnapshot.getKey();
                            if (friends.stt_user0==true && friends.stt_user1==false)
                            {
                                stt_friend.setText("Đã gửi lời mời");
                                status = 10;
                            }else if (friends.stt_user0==true && friends.stt_user1==true)
                            {
                                status = 11;
                                stt_friend.setText("Bạn bè");
                                next_mess.setVisibility(View.VISIBLE);
                            }else if (friends.stt_user0==false && friends.stt_user1==true)
                            {
                                status = 1;
                                stt_friend.setText("Chấp nhận kết bạn");
                            }else {
                                status = 0;
                                stt_friend.setText("Kết bạn");
                            }
                        }
                    }else if (friends.id_user1.equals(key_user))
                    {
                        if (friends.id_user0.equals(key_friend))
                        {
                            idchatzoom = dataSnapshot.getKey();
                            if (friends.stt_user0==true && friends.stt_user1==false)
                            {
                                stt_friend.setText("Đồng ý kết bạn");
                                status = 1;
                            }else if (friends.stt_user0==true && friends.stt_user1==true)
                            {
                                status = 11;
                                stt_friend.setText("Bạn bè");
                                next_mess.setVisibility(View.VISIBLE);
                            }else if (friends.stt_user0==false && friends.stt_user1==true)
                            {
                                status = 10;
                                stt_friend.setText("Đã gửi lời mời kết bạn");
                            }else {
                                status = 0;
                                stt_friend.setText("Kết bạn");
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        stt_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog(Gravity.CENTER);
            }
        });
        next_mess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference = FirebaseDatabase.getInstance().getReference("Friends");
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (final DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Friends friends = dataSnapshot.getValue(Friends.class);
                            if (friends.id_user0.equals(key_user))
                            {
                                if (friends.id_user1.equals(key_friend))
                                {
                                    idchatzoom = dataSnapshot.getKey();
                                    Intent intent = new Intent(ProfileUserActivity.this, ChatActivity.class);
                                    intent.putExtra("key_user",key_user);
                                    intent.putExtra("idchatzoom",idchatzoom);
                                    startActivity(intent);
                                }
                            }else if (friends.id_user1.equals(key_user))
                            {
                                if (friends.id_user0.equals(key_friend))
                                {
                                    idchatzoom = dataSnapshot.getKey();
                                    Intent intent = new Intent(ProfileUserActivity.this, ChatActivity.class);
                                    intent.putExtra("key_user",key_user);
                                    intent.putExtra("idchatzoom",idchatzoom);
                                    startActivity(intent);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    private void openDialog(int gra) {
        final Dialog dialog = new Dialog(ProfileUserActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_yes_or_no);

        Window window = dialog.getWindow();
        if (window == null){return;}
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams win = window.getAttributes();
        win.gravity = gra;
        window.setAttributes(win);

        if (Gravity.CENTER== gra){
            dialog.setCancelable(true);
        }else {
            dialog.setCancelable(false);
        }

        TextView textView = dialog.findViewById(R.id.question);
        Button yes = dialog.findViewById(R.id.yes);
        Button no = dialog.findViewById(R.id.no);

        if (status == 2107){
            textView.setText("Kết bạn");
        } else if (status==0){
            textView.setText("Kết bạn");
        }else if (status==1){
            textView.setText("Chấp nhận kết bạn");
        }else if (status==10){
            textView.setText("Hủy lời mời");
        }else if (status==11){
            textView.setText("Hủy kết bạn");
        }

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(ProfileUserActivity.this);
                progressDialog.show();
                progressDialog.setContentView(R.layout.progress_dialog);
                progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        dialog.dismiss();
                        if (status == 2107){
                            pushFriends();
                        } else if (status==0){
                            databaseReference = FirebaseDatabase.getInstance().getReference();
                            databaseReference.child("Friends").child(idchatzoom).child("id_user0").setValue(key_user);
                            databaseReference.child("Friends").child(idchatzoom).child("id_user1").setValue(key_friend);
                            databaseReference.child("Friends").child(idchatzoom).child("stt_user0").setValue(true);
                            databaseReference.child("Friends").child(idchatzoom).child("stt_user1").setValue(false);
                        }else if (status==1){
                            databaseReference = FirebaseDatabase.getInstance().getReference();
                            databaseReference.child("Friends").child(idchatzoom).child("stt_user0").setValue(true);
                            databaseReference.child("Friends").child(idchatzoom).child("stt_user1").setValue(true);
                            req_time_now();
                            databaseReference.child("Friends").child(idchatzoom).child("time").setValue(time_now);
                            begin_chat();
                        }else if (status==10){
                            databaseReference = FirebaseDatabase.getInstance().getReference();
                            databaseReference.child("Friends").child(idchatzoom).child("stt_user0").setValue(false);
                            databaseReference.child("Friends").child(idchatzoom).child("stt_user1").setValue(false);
                        }else if (status==11){
                            databaseReference = FirebaseDatabase.getInstance().getReference();
                            databaseReference.child("Friends").child(idchatzoom).child("stt_user0").setValue(false);
                            databaseReference.child("Friends").child(idchatzoom).child("stt_user1").setValue(false);
                        }
                    }
                }, 3000);


            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void pushFriends() {
                Friends friends = new Friends();
                friends.id_user0 = key_user;
                friends.id_user1 = key_friend;
                friends.stt_user0 = true;
                friends.stt_user1 = false;
                req_time_now();
                friends.time = time_now;
                databaseReference = FirebaseDatabase.getInstance().getReference();
                databaseReference.child("Friends").push().setValue(friends, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete( DatabaseError error, @NonNull DatabaseReference ref) {
                        databaseReference = FirebaseDatabase.getInstance().getReference();
                        ChatRoom chatRoom = new ChatRoom();
                        chatRoom.users = "" + key_user+ "/" + key_friend;
                        chatRoom.admin = "0";
                        chatRoom.avatar = "0";
                        chatRoom.status = "activity";
                        chatRoom.type ="0";
                        chatRoom.name = "0";
                        chatRoom.nikname = "0";
                        databaseReference.child("ChatRoom").child(idchatzoom).setValue(chatRoom, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                if (error==null){
                                    Toast.makeText(ProfileUserActivity.this, "hoàn thành", Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(ProfileUserActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
    }

    private void begin_chat() {
        Messenger messenger = new Messenger();
        messenger.sender = key_user;
        messenger.status = "hint";
        messenger.mess = "Đồng ý kết bạn";
        messenger.type = "text";
        req_time_now();
        messenger.time =time_send;
        messenger.day = day_send;
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Chat").child(idchatzoom).push().setValue(messenger, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                if (error==null){
                }else {
                    Toast.makeText(ProfileUserActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void req_time_now() {

        Date date = new Date();
        SimpleDateFormat dateFormatWithZone = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        day_send = dateFormatWithZone.format(date);

        SimpleDateFormat timeFormatWithZone = new SimpleDateFormat("HH:mm:ss",Locale.getDefault());
        time_send = timeFormatWithZone.format(date);

        time_now = ""+time_send+ "   "+day_send;
    }
    protected void set_status(String string){
        databaseReference= FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Users").child(key_user).child("status").setValue(string);
    }

    @Override
    protected void onResume() {
        super.onResume();
        set_status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        set_status("offline");
    }
}