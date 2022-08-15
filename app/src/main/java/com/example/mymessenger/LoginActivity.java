package com.example.mymessenger;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class LoginActivity extends AppCompatActivity {
    EditText phone_login,pass_login;
    TextView tv_pass_miss,tv_sig_up;
    Button btn_login;
    ImageView view_gg,view_fb;
    Intent intent;
    ProgressDialog progressDialog;
    DatabaseReference databaseReference;
    String login_phone="",login_pass="",login_email="",key_user="",token="",login_token="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tv_sig_up = (TextView) findViewById(R.id.signup);
        phone_login=(EditText) findViewById(R.id.editTextLoginTK);
        pass_login=(EditText)findViewById(R.id.editTextLoginMK);
        btn_login = (Button) findViewById(R.id.button_signin);
        tv_pass_miss = (TextView) findViewById(R.id.textpassmiss);

        view_gg = (ImageView) findViewById(R.id.google);
        view_fb = (ImageView) findViewById(R.id.facebook);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }

                        // Get new FCM registration token
                        token = task.getResult();
                    }
                });

        tv_pass_miss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(LoginActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.custom_dialog_dev);

                Window window = dialog.getWindow();
                if (window == null){return;}
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                WindowManager.LayoutParams win = window.getAttributes();
                win.gravity = Gravity.CENTER;
                window.setAttributes(win);
                dialog.setCancelable(true);
                dialog.show();
            }
        });
        view_gg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(LoginActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.custom_dialog_dev);

                Window window = dialog.getWindow();
                if (window == null){return;}
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                WindowManager.LayoutParams win = window.getAttributes();
                win.gravity = Gravity.CENTER;
                window.setAttributes(win);
                dialog.setCancelable(true);
                dialog.show();
            }
        });
        view_fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(LoginActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.custom_dialog_dev);

                Window window = dialog.getWindow();
                if (window == null){return;}
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                WindowManager.LayoutParams win = window.getAttributes();
                win.gravity = Gravity.CENTER;
                window.setAttributes(win);
                dialog.setCancelable(true);
                dialog.show();
            }
        });
        tv_sig_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phone_login.getText().toString().length() >0 && pass_login.getText().toString().length()>0)
                {
                    progressDialog = new ProgressDialog(LoginActivity.this);
                    progressDialog.show();
                    progressDialog.setContentView(R.layout.progress_dialog);
                    progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                    databaseReference = FirebaseDatabase.getInstance().getReference("Users");
                    databaseReference.orderByChild("phonenumber").equalTo(phone_login.getText().toString());
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot: snapshot.getChildren())
                            {
                                Users users = dataSnapshot.getValue(Users.class);
                                if (users.phonenumber.equals((phone_login.getText().toString().trim())))
                                {
                                    key_user= dataSnapshot.getKey();
                                    login_phone = users.phonenumber;
                                    login_pass = users.password;
                                    login_email = users.email;
                                    login_token = users.token;
                                    intent = new Intent(LoginActivity.this,MainActivity.class);
                                    intent.putExtra("key_user",key_user);
                                }

                            }
                            progressDialog.dismiss();

                            if (phone_login.getText().toString().equals(login_phone) & pass_login.getText().toString().equals(login_pass))
                            {
                                if (login_token.equals(token)){
                                    Toast.makeText(LoginActivity.this,"đăng nhập thành công",Toast.LENGTH_LONG).show();
                                    startActivity(intent);
                                    pass_login.setText("***");
                                    finish();
                                }else {
                                    set_token(key_user,token);
                                    Toast.makeText(LoginActivity.this,"đăng nhập thành công",Toast.LENGTH_LONG).show();
                                    startActivity(intent);

                                    pass_login.setText("***");
                                    finish();
                                }
                            }
                            else {
                                dialog_err_login(Gravity.CENTER);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
    }

    private void set_token(String key_user, String token) {
        databaseReference= FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Users").child(key_user).child("token").setValue(token);
    }

    private void dialog_err_login(int gravity) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_err_login);

        Window window= dialog.getWindow();
        if (window == null)
        {
            return;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);

        if (Gravity.CENTER==gravity)
        {

        }else {
            dialog.setCancelable(false);
        }

        dialog.show();
    }
}