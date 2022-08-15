package com.example.mymessenger.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.mymessenger.fragment.ContactActivity;
import com.example.mymessenger.fragment.MessengerActivity;
import com.example.mymessenger.fragment.StoryActivity;

public class ViewPageAdapter extends FragmentStatePagerAdapter {
    public ViewPageAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    public ViewPageAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new MessengerActivity();
            case 1:
                return new ContactActivity();
            case 2:
                return new StoryActivity();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
