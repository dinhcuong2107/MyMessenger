package com.example.mymessenger.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.appsearch.GetByDocumentIdRequest;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.mymessenger.MainActivity;
import com.example.mymessenger.R;
import com.example.mymessenger.adapter.UsersAdapter;
import com.example.mymessenger.obj.Friends;
import com.example.mymessenger.obj.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ContactActivity extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    String key_user;
    DatabaseReference databaseReference;
    EditText editText_find_user;
    SwipeRefreshLayout swipeRefreshLayout;
    ProgressDialog progressDialog;
    RecyclerView recyclerViewContact;
    UsersAdapter usersAdapter;
    List<Users> usersList;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_contact,container,false);

        editText_find_user = (EditText) view.findViewById(R.id.frag_contact_edt_search);
        recyclerViewContact = (RecyclerView) view.findViewById(R.id.frag_contact_recyclerview);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_contact);

        Bundle bundle = getActivity().getIntent().getExtras();
        key_user = bundle.getString("key_user");

        load_list_friend();
        swipeRefreshLayout.setOnRefreshListener(this);

        editText_find_user.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch();
                    return true;
                }
                return false;
            }
        });

        return view;
    }

    private void performSearch() {
        check_number_or_text();
    }

    private void check_number_or_text() {

        progressDialog = new ProgressDialog(getContext());
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        String string = editText_find_user.getText().toString();
        int l = string.length();

        boolean bResult = isNumber(string,l);
        if(bResult) {
            databaseReference = FirebaseDatabase.getInstance().getReference("Users");
            databaseReference.orderByChild("phonenumber").equalTo(string);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean check=false;
                    for (DataSnapshot dataSnapshot: snapshot.getChildren())
                    {
                        Users users = dataSnapshot.getValue(Users.class);
                        if (users.phonenumber.equals(string))
                        {
                            check = true;
                            break;
                        }else {
                            check =false;

                        }
                    }

                    Handler handler = new Handler();
                    boolean finalCheck = check;
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            if (finalCheck)
                            {
                                UsersAdapter usersAdapter = new UsersAdapter(getContext());
                                usersAdapter.notifyDataSetChanged();
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
                                recyclerViewContact.setLayoutManager(linearLayoutManager);

                                usersAdapter.setData(getListUser(""+editText_find_user.getText().toString(),true));
                                recyclerViewContact.setAdapter(usersAdapter);
                            }
                            else {
                                dialog_find_user(Gravity.CENTER);
                                load_list_friend();
                            }
                        }
                    }, 3000);

                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
//            Toast.makeText(MainActivity.this,"đang tìm kiếm   "+editText_find_user.getText().toString()+ "is number",Toast.LENGTH_LONG).show();
        }
        else{
            if (editText_find_user.getText().toString().equals("alluser"))
            {
                if (usersAdapter == null){    usersAdapter = new UsersAdapter(getContext()); } else{    usersAdapter.notifyDataSetChanged(); }

                usersAdapter.notifyDataSetChanged();
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
                recyclerViewContact.setLayoutManager(linearLayoutManager);

                usersAdapter.setData(getListUser(""+editText_find_user.getText().toString(),true));
                recyclerViewContact.setAdapter(usersAdapter);

            }
            Toast.makeText(getContext(),"đang tìm kiếm   "+editText_find_user.getText().toString()+ "is text",Toast.LENGTH_LONG).show();
        }
    }

    private List<Users> getListUser(String temp, Boolean phone) {
        List<Users> usersList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (final DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Users users = dataSnapshot.getValue(Users.class);
                    users.status = dataSnapshot.getKey();
                    users.position = key_user;
                    if (users.status.equals(key_user))
                    {}
                    else {
                        if (phone==true)
                        {
                            if (users.phonenumber.equals(temp))
                            {
                                usersList.add(users);
                            }
                        }else {
                            // tìm tên
                        }


                    }
                }
                recyclerViewContact.removeAllViews();
                usersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return usersList;
    }
//
    private boolean isNumber(String s, int l) {
        if(s == null || l == 0)
            return false;

        for(char c: s.toCharArray()) {
            if(!Character.isDigit(c))
                return false;
        }
        return true;
    }
//
//    private void load_list_mess() {
//    }
    private void load_list_friend() {
        usersAdapter = new UsersAdapter(getContext());
        usersAdapter.notifyDataSetChanged();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recyclerViewContact.setLayoutManager(linearLayoutManager);
        usersAdapter.setData(getListFriend());
        recyclerViewContact.setAdapter(usersAdapter);

    }
//
    private List<Users> getListFriend() {
        usersList = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("Friends");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();
                for (final DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Friends friends = dataSnapshot.getValue(Friends.class);
                    if (friends.id_user0.equals(key_user))
                    {
                        if (friends.stt_user0==true && friends.stt_user1==true)
                        {
                            Users users = new Users();
                            users.status = friends.id_user1;
                            users.position = friends.id_user0;
                            usersList.add(users);
                        }
                    } else if (friends.id_user1.equals(key_user))
                    {
                        if (friends.stt_user0==true && friends.stt_user1==true)
                        {
                            Users users = new Users();
                            users.status = friends.id_user0;
                            users.position = friends.id_user1;
                            usersList.add(users);
                        }
                    }
                }
                usersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return usersList;
    }

    private void dialog_find_user(int gravity) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_null_user);

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

    @Override
    public void onRefresh() {
        editText_find_user.setText("");
        load_list_friend();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 1000);
    }
}