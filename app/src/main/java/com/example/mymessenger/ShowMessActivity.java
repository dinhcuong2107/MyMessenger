package com.example.mymessenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mymessenger.obj.Messenger;
import com.example.mymessenger.obj.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ShowMessActivity extends AppCompatActivity {
String key="";
TextView textNameSender;
ImageView imageView,imageViewDownload;

DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_mess);

        textNameSender = (TextView) findViewById(R.id.showsender);
        imageView = (ImageView) findViewById(R.id.showimage);
        imageViewDownload = (ImageView) findViewById(R.id.showdownload);

        // nhận data từ intent
        key =  getIntent().getStringExtra("key");

        String[] parts = key.split("/");
        String part1 = parts[0];
        String part2 = parts[1];

        databaseReference = FirebaseDatabase.getInstance().getReference("Chat").child(part1).child(part2);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Messenger mess = snapshot.getValue(Messenger.class);
                if (mess.type.equals("text"))
                {
                }else if (mess.type.equals("image"))
                {
                    Picasso.with(imageView.getContext()).load(mess.mess).into(imageView);
                }else if (mess.type.equals("voice"))
                {
                }else if (mess.type.equals("call"))
                {
                }

                databaseReference= FirebaseDatabase.getInstance().getReference("Users").child(mess.sender);
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Users users = snapshot.getValue(Users.class);
                        textNameSender.setText(users.fullname);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}