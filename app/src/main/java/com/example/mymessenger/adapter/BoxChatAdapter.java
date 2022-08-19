package com.example.mymessenger.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymessenger.ChatActivity;
import com.example.mymessenger.R;
import com.example.mymessenger.obj.ChatRoom;
import com.example.mymessenger.obj.Messenger;
import com.example.mymessenger.obj.Users;
import com.google.android.gms.common.api.internal.LifecycleFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BoxChatAdapter extends RecyclerView.Adapter<BoxChatAdapter.BoxChatViewHolder>{
    List<ChatRoom> listChat;
    Context context;
    DatabaseReference databaseReference,databaseReferenceUsers,databaseReferenceMess;
    String key_user="",idchatzoom="";

    public BoxChatAdapter(Context context) {
        this.context = context;
    }
    public void setData(List<ChatRoom> list){
        this.listChat = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BoxChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("key_user",key_user);
                intent.putExtra("idchatzoom",idchatzoom);
                context.startActivities(new Intent[]{intent});
            }
        });
        return new BoxChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BoxChatViewHolder holder, int position) {
        ChatRoom chatRoom = listChat.get(position);
        if (chatRoom==null)
        {
            return;
        }
        key_user = chatRoom.status;
        idchatzoom = chatRoom.name;
        databaseReference = FirebaseDatabase.getInstance().getReference("ChatRoom").child(chatRoom.name);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ChatRoom boxchat = snapshot.getValue(ChatRoom.class);

                if (boxchat.status.equals("activity")){

                    if (boxchat.type.equals("0")){
                        String[] users = boxchat.users.split("/");
                        for (int i = 0; i <= 1; i++){
                            if (users[i].equals(key_user)){
                            }else {
                                if (boxchat.nikname.equals("0")){
                                    // get name_friend
                                    databaseReferenceUsers= FirebaseDatabase.getInstance().getReference("Users").child(users[i]);
                                    databaseReferenceUsers.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            Users users = snapshot.getValue(Users.class);
                                            Picasso.with(holder.imageView.getContext()).load(users.avatar).into(holder.imageView);
                                            holder.text_name.setText(users.fullname);
                                            if (users.status.equals("online"))
                                            {
                                                holder.img_stt.setBackgroundColor(Color.parseColor("#00FF0A"));
                                            }else {
                                                holder.img_stt.setBackgroundColor(Color.parseColor("#000000"));
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }else {
                                    String[] namebox = boxchat.nikname.split("/");
                                    holder.text_name.setText(namebox[i]);
                                }
                            }
                        }
                    }else {

                        Picasso.with(holder.imageView.getContext()).load(boxchat.avatar).into(holder.imageView);
                        holder.text_name.setText(boxchat.name);

                        // set status
                        int count=0;
                        String string = boxchat.users;
                        for (int i = 0; i < string.length(); i++) {
                            // Nếu ký tự tại vị trí thứ i bằng '/' thì tăng count lên 1
                            if (string.charAt(i) == '/') {
                                count++;
                            }
                        }
                        String[] users = boxchat.users.split("/");

                        final Boolean[] online = {false};
                        for (int i = 0; i <= count; i++){
                            if(users[i].equals(key_user)){}else {
                                if (online[0] ==false){
                                    databaseReferenceUsers = FirebaseDatabase.getInstance().getReference("Users").child(users[i]);
                                    databaseReferenceUsers.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            Users users = snapshot.getValue(Users.class);

                                            if (users.status.equals("online")) {
                                                online[0] = true;
                                                holder.img_stt.setBackgroundColor(Color.parseColor("#00FF0A"));
                                            }else {
                                                online[0] = false;
                                                holder.img_stt.setBackgroundColor(Color.parseColor("#000000"));
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                                else {
                                    holder.img_stt.setBackgroundColor(Color.parseColor("#00FF0A"));
                                }

                            }
                        }


                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("Chat").child(chatRoom.name);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (final DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Messenger messenger = dataSnapshot.getValue(Messenger.class);
                    if (messenger.status.equals("show"))
                    {
                        if (messenger.mess.length()>21)
                        {
                            messenger.mess = messenger.mess.substring(0,21)+" ...";
                        }
                        if (messenger.type.equals("text")){
                            holder.text_mess.setText(messenger.mess);
                        }else if (messenger.type.equals("image")){
                        holder.text_mess.setText("Tin nhắn hình ảnh");
                        }else if (messenger.type.equals("voice")){
                            holder.text_mess.setText("Tin nhắn thoại");
                        }else if (messenger.type.equals("file")){
                            holder.text_mess.setText("Đã gửi file");
                        }else if (messenger.type.equals("call")){
                            holder.text_mess.setText("Call");
                        }


                        Date date = new Date();
                        SimpleDateFormat dateFormatWithZone = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        String day_now = dateFormatWithZone.format(date);
                        if (messenger.day.equals(day_now))
                        {
                            holder.text_time.setText(messenger.time);
                        }else {
                            holder.text_time.setText(messenger.day);
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        if (listChat != null){
            return listChat.size();
        }
        return 0;
    }

    public class BoxChatViewHolder extends RecyclerView.ViewHolder{
        private ImageView imageView,img_stt;
        private TextView text_name,text_mess,text_time;
        public BoxChatViewHolder(@NonNull View itemview){
            super(itemview);
            imageView = itemView.findViewById(R.id.item_chat_img_avt);
            img_stt = itemView.findViewById(R.id.item_chat_img_stt);
            text_name = itemView.findViewById(R.id.item_chat_textname);
            text_mess = itemView.findViewById(R.id.item_chat_textmess);
            text_time = itemView.findViewById(R.id.item_chat_texttime);
        }
    }
}
