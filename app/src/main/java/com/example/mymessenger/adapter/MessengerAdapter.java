package com.example.mymessenger.adapter;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymessenger.ChatActivity;
import com.example.mymessenger.ProfileUserActivity;
import com.example.mymessenger.R;
import com.example.mymessenger.obj.Friends;
import com.example.mymessenger.obj.Messenger;
import com.example.mymessenger.obj.Users;
import com.google.android.gms.common.api.internal.LifecycleFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.List;


public class MessengerAdapter extends RecyclerView.Adapter<MessengerAdapter.MessViewHolder>{

    List<Messenger> messengersList;
    Context context;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DatabaseReference databaseReference;
    String time_now="",key_user="",idchatzoom="";
    int TYPE_MESS;
    public MessengerAdapter(Context context) {
        this.context = context;
    }
    public void setData(List<Messenger> list){
        this.messengersList = list;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public MessViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (TYPE_MESS==0)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sent, parent, false);
            return new MessengerAdapter.MessViewHolder(view);
        }else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_receive, parent, false);
            return new MessengerAdapter.MessViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessViewHolder holder, int position) {

        holder.jpg.setVisibility(View.GONE);
        holder.text_content.setVisibility(View.GONE);
        holder.l_voice.setVisibility(View.GONE);
        holder.l_call.setVisibility(View.GONE);

        Messenger messenger = messengersList.get(position);

        if (messenger.status.equals("00:00")) {
            holder.text_date.setVisibility(View.VISIBLE);
        }
        else {
            holder.text_date.setVisibility(View.GONE);
        }

        if (messenger.type.equals(messenger.sender)) {
            holder.text_sender.setVisibility(View.GONE);
        }
        else {
            holder.text_sender.setVisibility(View.VISIBLE);
        }

        String[] parts = messenger.mess.split("/");
        String part1 = parts[0];
        String part2 = parts[1];

        databaseReference= FirebaseDatabase.getInstance().getReference("Users").child(messenger.sender);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);
                Picasso.with(holder.imageView.getContext()).load(users.avatar).into(holder.imageView);
                holder.text_sender.setText(users.fullname);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("Chat").child(part1).child(part2);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Messenger mess = snapshot.getValue(Messenger.class);
                if (mess.type.equals("text"))
                {
                    holder.text_content.setText(mess.mess);
                    holder.text_content.setVisibility(View.VISIBLE);
                }else if (mess.type.equals("image"))
                {
                    Picasso.with(holder.jpg.getContext()).load(mess.mess).into(holder.jpg);
                    holder.jpg.setVisibility(View.VISIBLE);
                }else if (mess.type.equals("voice"))
                {
                    holder.l_voice.setVisibility(View.VISIBLE);
                }else if (mess.type.equals("call"))
                {
                    holder.time_call.setText(mess.mess);

                    holder.l_call.setVisibility(View.VISIBLE);
                }

                holder.text_time.setText(mess.time);
                holder.text_date.setText(mess.day);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.jpg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show ảnh
            }
        });
        holder.l_voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // play voice
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        Messenger messenger = messengersList.get(position);
        if (messenger.sender.equals(messenger.type))
        {
            TYPE_MESS =0;
        }else {TYPE_MESS = 1;}
        return TYPE_MESS;
    }

    @Override
    public int getItemCount() {
        if (messengersList != null){
            return messengersList.size();
        }
        return 0;
    }

    public class MessViewHolder extends RecyclerView.ViewHolder{
        private ImageView imageView,jpg;
        private LinearLayout l_voice,l_call;
        private TextView text_content,text_time,text_date,text_sender,time_call;

        public MessViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_img_avt_sender);
            l_call = itemView.findViewById(R.id.item_linear_call);
            l_voice = itemView.findViewById(R.id.item_linear_voice);
            jpg = itemView.findViewById(R.id.item_img);
            text_content = itemView.findViewById(R.id.item_text_content);
            text_time = itemView.findViewById(R.id.item_text_time);
            text_date = itemView.findViewById(R.id.item_text_date);
            text_sender = itemView.findViewById(R.id.item_text_sender);
            time_call = itemView.findViewById(R.id.item_text_call);
        }
    }
}
