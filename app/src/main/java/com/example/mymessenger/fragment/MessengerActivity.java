package com.example.mymessenger.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.mymessenger.R;
import com.example.mymessenger.adapter.BoxChatAdapter;
import com.example.mymessenger.adapter.MessengerAdapter;
import com.example.mymessenger.obj.ChatRoom;
import com.example.mymessenger.obj.Messenger;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MessengerActivity extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
String key_user="";
DatabaseReference databaseReferenceMessenger,databaseReference;
EditText editText_find_user;
ProgressDialog progressDialog;
RecyclerView recyclerViewMessenger;
SwipeRefreshLayout swipeRefreshLayout;

    List<ChatRoom> list = new ArrayList<>();
    BoxChatAdapter boxChatAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_messenger,container,false);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_messenger);
        editText_find_user = (EditText) view.findViewById(R.id.frag_messenger_edt_search);
        recyclerViewMessenger = (RecyclerView) view.findViewById(R.id.frag_messenger_recyclerview);

        Bundle bundle = getActivity().getIntent().getExtras();
        key_user = bundle.getString("key_user");

        swipeRefreshLayout.setOnRefreshListener(this);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        databaseReference = FirebaseDatabase.getInstance().getReference("ChatRoom");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                String temp = "0";
                for (final DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ChatRoom boxchat = dataSnapshot.getValue(ChatRoom.class);

                    int count=0;
                    String string = boxchat.users;
                    for (int i = 0; i < string.length(); i++) {
                        // Nếu ký tự tại vị trí thứ i bằng 'a' thì tăng count lên 1
                        if (string.charAt(i) == '/') {
                            count++;
                        }
                    }

                    String[] users = boxchat.users.split("/");
                    for (int i = 0; i <= count; i++){
                        if (users[i].equals(key_user)){
                            if (boxchat.status.equals("activity")){

                                boxchat.name = dataSnapshot.getKey();
                                boxchat.status = key_user;
                                ChatRoom chatRoom = boxchat;
                                list.add(chatRoom);

                            }
                        }
                    }
                }
                load_list_boxchat();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void load_list_boxchat() {
        boxChatAdapter = new BoxChatAdapter(getContext());
        recyclerViewMessenger.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerViewMessenger.setLayoutManager(linearLayoutManager);
        boxChatAdapter.setData(list);
        recyclerViewMessenger.setAdapter(boxChatAdapter);
    }

    @Override
    public void onRefresh() {
        editText_find_user.setText("");
        load_list_boxchat();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 1000);
    }
}