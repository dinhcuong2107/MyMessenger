package com.example.mymessenger;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mymessenger.obj.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

public class SplashActivity extends AppCompatActivity {
    Handler handler;
    ImageView img;
    String token="",login_token="", key_user="";
    DatabaseReference databaseReference;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        img = findViewById(R.id.imgsplash);

        img.animate().alpha(5000).setDuration(0);
        handler =new Handler();
        startAnimation();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                FirebaseMessaging.getInstance().getToken()
                        .addOnCompleteListener(new OnCompleteListener<String>() {
                            @Override
                            public void onComplete(@NonNull Task<String> task) {
                                if (!task.isSuccessful()) {
                                    return;
                                }

                                // Get new FCM registration token
                                token = task.getResult();

                                databaseReference = FirebaseDatabase.getInstance().getReference("Users");
                                databaseReference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot dataSnapshot: snapshot.getChildren())
                                        {
                                            Users users = dataSnapshot.getValue(Users.class);
                                            if (users.token.equals((token)))
                                            {
                                                key_user= dataSnapshot.getKey();
                                                login_token = users.token;
                                                intent = new Intent(SplashActivity.this,MainActivity.class);
                                                intent.putExtra("key_user",key_user);

                                            }
                                        }
                                        if (login_token.equals(token)){
                                            startActivity(intent);
                                            finish();
                                        }else {
                                            Intent dsp = new Intent(SplashActivity.this, LoginActivity.class);
                                            startActivity(dsp);
                                            finish();
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });
            }
        },2500);
    }


    private void startAnimation() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                img.animate().rotationBy(360).withEndAction(this).setDuration(0).setInterpolator(new LinearInterpolator()).start();
            }
        };
        img.animate().rotationBy(360).withEndAction(runnable).setDuration(1000).setInterpolator(new LinearInterpolator()).start();
    }
}