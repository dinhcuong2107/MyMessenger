package com.example.mymessenger.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymessenger.R;
import com.google.firebase.database.DatabaseReference;

public class MessengerActivity extends Fragment {
String key_user="";
DatabaseReference databaseReferenceMessenger,databaseReference;
EditText editText_find_user;
ProgressDialog progressDialog;
RecyclerView recyclerViewMessenger;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_messenger,container,false);


        editText_find_user = (EditText) view.findViewById(R.id.frag_messenger_edt_search);
        recyclerViewMessenger = (RecyclerView) view.findViewById(R.id.frag_messenger_recyclerview);

        Bundle bundle = getActivity().getIntent().getExtras();
        key_user = bundle.getString("key_user");

        return view;
    }
}