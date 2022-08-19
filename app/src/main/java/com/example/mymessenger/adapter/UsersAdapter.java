package com.example.mymessenger.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymessenger.ChatActivity;
import com.example.mymessenger.ProfileUserActivity;
import com.example.mymessenger.R;
import com.example.mymessenger.obj.Friends;
import com.example.mymessenger.obj.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;


public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersViewHolder>{

    List<Users> usersList;
    Context context;
    DatabaseReference databaseReference;
    String idchatzoom="",key_friend="",key_user="";

    public UsersAdapter(Context context) {
        this.context = context;
    }
    public void setData(List<Users> list){
        this.usersList = list;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Boolean[] check_friend = {false};
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
                                    if (friends.stt_user0==true && friends.stt_user1==true)
                                    {
                                        check_friend[0] =true;
                                    }
                                }
                            }else if (friends.id_user1.equals(key_user))
                            {
                                if (friends.id_user0.equals(key_friend))
                                {
                                    idchatzoom = dataSnapshot.getKey();
                                    if (friends.stt_user0==true && friends.stt_user1==true){
                                        check_friend[0] =true;
                                    }
                                }
                            }
                        }
                        if (check_friend [0] ==  false)
                        {
                            Intent intent = new Intent(context, ProfileUserActivity.class);
                            intent.putExtra("key_user",key_user);
                            intent.putExtra("key_friend",key_friend);
                            context.startActivities(new Intent[]{intent});
                        }
                        else {
                            Intent intent = new Intent(context, ChatActivity.class);
                            intent.putExtra("key_user",key_user);
                            intent.putExtra("idchatzoom",idchatzoom);
                            context.startActivities(new Intent[]{intent});
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });
        return new UsersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {
        Users users = usersList.get(position);
        key_user = users.position;
        key_friend = users.status;
        if (users==null)
        {
            return;
        }
        databaseReference= FirebaseDatabase.getInstance().getReference("Users").child(users.status);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users_item = snapshot.getValue(Users.class);
                Picasso.with(holder.imageView.getContext()).load(users_item.avatar).into(holder.imageView);
                holder.textView.setText(users_item.fullname);
                if (users_item.status.equals("online")){
                    holder.img_stt.setBackgroundColor(Color.parseColor("#00FF0A"));
                } else {
                    holder.img_stt.setBackgroundColor(Color.parseColor("#000000"));
                }
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
                    if (friends.id_user0.equals(users.position))
                    {
                        if (friends.id_user1.equals(users.status))
                        {
                            idchatzoom = dataSnapshot.getKey();
                            if (friends.stt_user0==true && friends.stt_user1==false)
                            {
                                holder.status_friend.setText("Đã gửi lời mời");
                            }else if (friends.stt_user0==true && friends.stt_user1==true)
                            {
                                holder.status_friend.setText("Bạn bè");
                            }else if (friends.stt_user0==false && friends.stt_user1==true)
                            {
                                holder.status_friend.setText("Click here to chấp nhận kết bạn");
                            }else {
                                holder.status_friend.setText("Click here to add friend");
                            }
                        }
                    }else if (friends.id_user1.equals(users.position))
                    {
                        if (friends.id_user0.equals(users.status))
                        {
                            idchatzoom = dataSnapshot.getKey();
                            if (friends.stt_user0==true && friends.stt_user1==false){
                                holder.status_friend.setText("Click here to chấp nhận kết bạn");
                            }else if (friends.stt_user0==true && friends.stt_user1==true){
                                holder.status_friend.setText("Bạn bè");
                            }else if (friends.stt_user0==false && friends.stt_user1==true){
                                holder.status_friend.setText("Đã gửi lời mời");
                            }else {
                                holder.status_friend.setText("Click here to add friend");
                            }
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
        if (usersList != null){
            return usersList.size();
        }
        return 0;
    }

    public class UsersViewHolder extends RecyclerView.ViewHolder{
        private ImageView imageView,img_stt;
        private TextView textView,status_friend;

        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_contact_img_avt_user);
            img_stt = itemView.findViewById(R.id.item_contact_img_stt);
            textView = itemView.findViewById(R.id.item_contact_text_name_user);
            status_friend = itemView.findViewById(R.id.item_contact_text_stt_friend);
        }
    }
}
