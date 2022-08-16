package com.example.mymessenger.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymessenger.R;

public class BoxChatAdapter extends RecyclerView.Adapter<BoxChatAdapter.BoxChatViewHolder>{


    @NonNull
    @Override
    public BoxChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new BoxChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BoxChatViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class BoxChatViewHolder extends RecyclerView.ViewHolder{
        private ImageView imageView,img_stt;
        private TextView text_name,text_mess,text_time;
        public BoxChatViewHolder(@NonNull View itemview){
            super(itemview);
            imageView = itemView.findViewById(R.id.item_chat_img_avt);
            img_stt = itemView.findViewById(R.id.item_chat_img_stt);
            text_name = itemView.findViewById(R.id.item_chat_textname);
            text_mess = itemView.findViewById(R.id.item_chat_textmess);
            text_time = itemView.findViewById(R.id.item_chat_texttime);
        }
    }
}
