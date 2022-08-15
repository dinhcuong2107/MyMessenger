package com.example.mymessenger.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mymessenger.R;

public class StoryActivity extends Fragment {
String key_user="";
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_story,container,false);

        Bundle bundle = getActivity().getIntent().getExtras();
        key_user = bundle.getString("key_user");

        return view;
    }
}