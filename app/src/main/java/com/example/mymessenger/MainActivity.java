package com.example.mymessenger;

import static android.text.TextUtils.isEmpty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mymessenger.adapter.UsersAdapter;
import com.example.mymessenger.adapter.ViewPageAdapter;
import com.example.mymessenger.fragment.ContactActivity;
import com.example.mymessenger.fragment.MessengerActivity;
import com.example.mymessenger.fragment.StoryActivity;
import com.example.mymessenger.obj.Friends;
import com.example.mymessenger.obj.Users;
import com.example.mymessenger.service.MyFirebaseMessagingService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
String key_user;
DatabaseReference databaseReferenceUser,databaseReference;
BottomNavigationView bottomNavigationView;
ViewPager viewPager;
ProgressDialog progressDialog;
ImageView imageView;
TextView fullname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.img_avt_user);
        fullname = (TextView) findViewById(R.id.text_name_user);

        // nhận data từ intent
        key_user =  getIntent().getStringExtra("key_user");

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView_main);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        load_view_pager();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_messenger:
                        viewPager.setCurrentItem(0);
                        Intent intent0 = new Intent(MainActivity.this, MessengerActivity.class);
                        intent0.putExtra("key_user",key_user);
                        Toast.makeText(MainActivity.this,""+item.getTitle(),Toast.LENGTH_LONG).show();
                        break;
                    case R.id.menu_contacts:
                        viewPager.setCurrentItem(1);
                        Intent intent1 = new Intent(MainActivity.this, ContactActivity.class);
                        intent1.putExtra("key_user",key_user);
                        Toast.makeText(MainActivity.this,""+item.getTitle(),Toast.LENGTH_LONG).show();
                        break;
                    case R.id.menu_story:
                        viewPager.setCurrentItem(2);
                        Intent intent2 = new Intent(MainActivity.this, StoryActivity.class);
                        intent2.putExtra("key_user",key_user);
                        Toast.makeText(MainActivity.this,""+item.getTitle(),Toast.LENGTH_LONG).show();
                        break;
                }
                return true;
            }
        });

        databaseReferenceUser= FirebaseDatabase.getInstance().getReference("Users").child(key_user);
        databaseReferenceUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);
                if (users.token.equals("null"))
                {
                    Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
                if (users.avatar.length() != 0)
                {
                    Picasso.with(MainActivity.this).load(users.avatar).into(imageView);
                }
                fullname.setText(""+users.fullname);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog_Menu(Gravity.CENTER);
            }
        });
    }

    private void load_view_pager() {
        ViewPageAdapter viewPageAdapter = new ViewPageAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(viewPageAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        bottomNavigationView.getMenu().findItem(R.id.menu_messenger).setChecked(true);
                        break;
                    case 1:
                        bottomNavigationView.getMenu().findItem(R.id.menu_contacts).setChecked(true);
                        break;
                    case 2:
                        bottomNavigationView.getMenu().findItem(R.id.menu_story).setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void openDialog_Menu(int gra) {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_menu);

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
        ImageView menu_imageView = dialog.findViewById(R.id.menu_avt);
        TextView textView = dialog.findViewById(R.id.menu_fullname);

        LinearLayout menu_back = dialog.findViewById(R.id.menu_back);
        LinearLayout menu_avatar = dialog.findViewById(R.id.menu_avatar);
        LinearLayout menu_wait = dialog.findViewById(R.id.menu_wait);
        LinearLayout menu_setting = dialog.findViewById(R.id.menu_setting);
        LinearLayout menu_logout = dialog.findViewById(R.id.menu_logout);

        menu_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        menu_wait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        menu_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference= FirebaseDatabase.getInstance().getReference();
                databaseReference.child("Users").child(key_user).child("token").setValue("null");

                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.show();
                progressDialog.setContentView(R.layout.progress_dialog);
                progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                SystemClock.sleep(500); //ms

                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        databaseReferenceUser= FirebaseDatabase.getInstance().getReference("Users").child(key_user);
        databaseReferenceUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);

                if (users.avatar.length() != 0)
                {
                    Picasso.with(MainActivity.this).load(users.avatar).into(menu_imageView);
                }
                textView.setText(""+users.fullname);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        dialog.show();
    }

}