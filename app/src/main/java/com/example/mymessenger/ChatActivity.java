package com.example.mymessenger;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.icu.util.Calendar;
import android.icu.util.Measure;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.mymessenger.adapter.MessengerAdapter;
import com.example.mymessenger.adapter.UsersAdapter;
import com.example.mymessenger.obj.ChatRoom;
import com.example.mymessenger.obj.Messenger;
import com.example.mymessenger.obj.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
String key_user="",time_send="",day_send="",idchatzoom="";
DatabaseReference databaseReference;
ImageView imageView,call,videocall,setting;
Button camera,photo,voice,send;
EditText editText;
TextView nameFriend,sstFriend;

List<Messenger> list = new ArrayList<>();
MessengerAdapter messengerAdapter;
RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        imageView = (ImageView) findViewById(R.id.img_mess_avt);
        call = (ImageView) findViewById(R.id.img_mess_call);
        videocall = (ImageView) findViewById(R.id.img_mess_videocall);
        setting = (ImageView) findViewById(R.id.img_mess_setting);

        nameFriend = (TextView) findViewById(R.id.text_mess_name);
        sstFriend = (TextView) findViewById(R.id.text_mess_stt);

        camera = (Button) findViewById(R.id.mess_new_photo);
        photo = (Button) findViewById(R.id.mess_photo);
        voice = (Button) findViewById(R.id.mess_new_voice);
        send = (Button) findViewById(R.id.mess_button_send);
        editText = (EditText) findViewById(R.id.mess_new_text);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_mess);

        // nhận data từ intent
        key_user =  getIntent().getStringExtra("key_user");
        idchatzoom =  getIntent().getStringExtra("idchatzoom");

        databaseReference = FirebaseDatabase.getInstance().getReference("ChatRoom").child(idchatzoom);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ChatRoom chatRoom = snapshot.getValue(ChatRoom.class);
                if (chatRoom.type.equals("0"))
                {
                    //tách tên - xác định friend
                }else{
                    // setting theo nhóm chát
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.length()>0)
                {
                    Messenger messenger = new Messenger();
                    messenger.sender = key_user;
                    messenger.status = "show";
                    messenger.mess = editText.getText().toString();
                    messenger.type = "text";
                    req_time_now();
                    messenger.time =time_send;
                    messenger.day = day_send;
                    databaseReference = FirebaseDatabase.getInstance().getReference();
                    databaseReference.child("Chat").child(idchatzoom).push().setValue(messenger, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            if (error==null){
                                editText.cancelPendingInputEvents();
                                editText.setText("");
                            }else {
                                Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        databaseReference = FirebaseDatabase.getInstance().getReference("Chat").child(idchatzoom);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                String temp = "0";
                for (final DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Messenger messenger = dataSnapshot.getValue(Messenger.class);
                    messenger.type = key_user;
                    messenger.mess ="" + idchatzoom+ "/"+dataSnapshot.getKey();

                    if (messenger.status.equals("show"))
                    {
                        if(messenger.day.equals(temp))
                        {}else {messenger.status="00:00";}
                        list.add(messenger);
                        messengerAdapter.notifyDataSetChanged();
                        recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount()-1);
                    }temp = messenger.day;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        load_content_mess();
    }

    private void load_content_mess() {
        messengerAdapter = new MessengerAdapter(getApplicationContext());
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        messengerAdapter.setData(list);
        recyclerView.setAdapter(messengerAdapter);
    }

    private void req_time_now() {
        Date date = new Date();
        SimpleDateFormat dateFormatWithZone = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        day_send = dateFormatWithZone.format(date);

        SimpleDateFormat timeFormatWithZone = new SimpleDateFormat("HH:mm:ss",Locale.getDefault());
        time_send = timeFormatWithZone.format(date);
    }
}