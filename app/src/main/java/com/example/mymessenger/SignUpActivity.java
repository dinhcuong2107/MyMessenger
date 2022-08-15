package com.example.mymessenger;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mymessenger.obj.Users;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class SignUpActivity extends AppCompatActivity {
TextView textViewSignin;
ImageView imageView;
DatabaseReference databaseReference,databaseReferenceCheck;
EditText edt_name, edt_phone,edt_email, edt_pw, edt_pw_again, edt_address;
RadioButton r_B, r_G;
Boolean temp_ck_phone,temp_ck_email;
Button btn_dob, btn_SignUp;
String urla="",time_now;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;
    ProgressDialog progressDialog;
    DatePickerDialog.OnDateSetListener dateSetListener;

    Calendar calendar = Calendar.getInstance();
    final int year = calendar.get(Calendar.YEAR);
    final int month = calendar.get(Calendar.MONTH);
    final int day = calendar.get(Calendar.DAY_OF_MONTH);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        textViewSignin = (TextView) findViewById(R.id.signin);

        imageView = (ImageView) findViewById(R.id.image_signup_avatar);

        edt_name = (EditText) findViewById(R.id.edt_signup_fullname);
        edt_phone = (EditText) findViewById(R.id.edt_signup_phone);
        edt_email = (EditText) findViewById(R.id.edt_signup_email);
        edt_pw = (EditText) findViewById(R.id.edt_signup_pw);
        edt_pw_again = (EditText) findViewById(R.id.edt_signup_pw_again);
        edt_address = (EditText) findViewById(R.id.edt_signup_address);

        r_B = (RadioButton) findViewById(R.id.radio_signup_boy);
        r_G = (RadioButton) findViewById(R.id.radio_signup_girl);

        btn_dob = (Button) findViewById(R.id.btn_signup_dob);
        btn_SignUp = (Button) findViewById(R.id.btn_signup);

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month = month+1;
                String temp_date = day+"/"+month+"/"+year;
                btn_dob.setText(temp_date);
            }
        };

        textViewSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });
        btn_dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(SignUpActivity.this,android.R.style.Theme_Holo_Light_Dialog_MinWidth,dateSetListener,year,month,day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });
        btn_SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (urla.length()==0){
                    Toast.makeText(SignUpActivity.this, "Bạn chưa thiết lập ảnh đại diện",Toast.LENGTH_LONG).show();
                }else if (edt_name.getText().toString().trim().isEmpty()){
                    Toast.makeText(SignUpActivity.this, "Bạn chưa thiết lập Tên",Toast.LENGTH_LONG).show();
                }else if (r_B.isChecked()==false && r_G.isChecked()==false){
                    Toast.makeText(SignUpActivity.this, "Bạn chưa thiết lập giới tính",Toast.LENGTH_LONG).show();
                }else if (btn_dob.getText().toString().trim().isEmpty()){
                    Toast.makeText(SignUpActivity.this, "Bạn chưa thiết lập Ngày sinh",Toast.LENGTH_LONG).show();
                }else if (edt_phone.getText().toString().trim().isEmpty()){
                    Toast.makeText(SignUpActivity.this, "Bạn chưa thiết lập Số điện thoại",Toast.LENGTH_LONG).show();
                }else if (edt_email.getText().toString().trim().isEmpty()){
                    Toast.makeText(SignUpActivity.this, "Bạn chưa thiết lập Email",Toast.LENGTH_LONG).show();
                }else if (edt_pw.getText().toString().trim().isEmpty()){
                    Toast.makeText(SignUpActivity.this, "Bạn chưa thiết lập Mật khẩu",Toast.LENGTH_LONG).show();
                }else if (edt_address.getText().toString().trim().isEmpty()){
                    Toast.makeText(SignUpActivity.this, "Nhâp lại địa chỉ",Toast.LENGTH_LONG).show();
                }else if (edt_pw.getText().toString().equals(edt_pw_again.getText().toString()) )
                {
                    if(edt_phone.length()==10)
                    {
                        checkPhone();
                    }
                    else {
                        Toast.makeText(SignUpActivity.this, "Số điện thoại không đúng",Toast.LENGTH_LONG).show();
                    }

                }else {
                    Toast.makeText(SignUpActivity.this, "Mật khẩu không trùng khớp",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void checkPhone() {
        progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        databaseReferenceCheck = FirebaseDatabase.getInstance().getReference("Users");
        databaseReferenceCheck.orderByChild("phonenumber").equalTo(edt_phone.getText().toString());
        databaseReferenceCheck.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren())
                {
                    Users users = dataSnapshot.getValue(Users.class);
                    if (users.phonenumber.equals(edt_phone.getText().toString().trim()))
                    {
                        temp_ck_phone = false;
                        Toast.makeText(SignUpActivity.this, "Số điên thoại đã được sử dụng",Toast.LENGTH_LONG).show();
                        break;
                    }else {
                        temp_ck_phone = true;
                    }
                }
                progressDialog.dismiss();
                if (temp_ck_phone==true)
                {
                    checkEmail();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void creating() {
        Users user_add = new Users();
        if (r_B.isChecked() )
        {
            user_add.sex="Nam";
        }
        if (r_G.isChecked())
        {
            user_add.sex="Nữ";
        }
        req_time_now();
        user_add.fullname = edt_name.getText().toString();
        user_add.avatar = urla;
        user_add.token = "";
        user_add.status = "0";
        user_add.email = edt_email.getText().toString();
        user_add.dateofbirth=btn_dob.getText().toString();
        user_add.phonenumber=edt_phone.getText().toString();
        user_add.password = edt_pw.getText().toString();
        user_add.position = "user";
        user_add.time_start = time_now;
        user_add.address = edt_address.getText().toString();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Users").push().setValue(user_add, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                if (error==null){
                    Toast.makeText(SignUpActivity.this, "hoàn thành", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignUpActivity.this,LoginActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Toast.makeText(SignUpActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void req_time_now() {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour_now = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute_now = mcurrentTime.get(Calendar.MINUTE);

        time_now = ""+hour_now+":"+minute_now + "   "+day+"/"+month+"/"+year;
    }

    private void checkEmail() {
        String email = edt_email.getText().toString().trim();

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

// onClick of button perform this simplest code.
        if (email.matches(emailPattern))
        {
            progressDialog = new ProgressDialog(SignUpActivity.this);
            progressDialog.show();
            progressDialog.setContentView(R.layout.progress_dialog);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            databaseReferenceCheck = FirebaseDatabase.getInstance().getReference("Users");
            databaseReferenceCheck.orderByChild("email").equalTo(edt_email.getText().toString());
            databaseReferenceCheck.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren())
                    {
                        Users users = dataSnapshot.getValue(Users.class);
                        if (users.email.equals(edt_email.getText().toString().trim()))
                        {
                            temp_ck_email = false;
                            Toast.makeText(SignUpActivity.this, "Email đã được sử dụng",Toast.LENGTH_LONG).show();
                            break;
                        }else {
                            temp_ck_email = true;

                        }
                    }
                    progressDialog.dismiss();
                    if (temp_ck_email==true){
                        creating();
                    }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else
        {
            Toast.makeText(SignUpActivity.this, "Email không hợp lệ",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);

                FirebaseStorage storage;
                StorageReference storageReference;
                storage = FirebaseStorage.getInstance();
                storageReference = storage.getReference();
                final StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
                if(filePath != null)
                {
                    final ProgressDialog progressDialog = new ProgressDialog(SignUpActivity.this);
                    progressDialog.setTitle("Uploading...");
                    progressDialog.show();

                    ref.putFile(filePath)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    progressDialog.dismiss();
                                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Uri downloadUrl = uri;
                                            urla=downloadUrl.toString();
                                        }
                                    });
                                    Toast.makeText(SignUpActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(SignUpActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                    double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                            .getTotalByteCount());
                                    progressDialog.setMessage("Uploaded "+(int)progress+"%");
                                }
                            });
                }
            }catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}