package com.example.mymessenger.obj;

public class ChatRoom {
    public String users;
    public String admin;
    public String status;
    public String avatar;
    public String type;
    public String name;
    public String nikname;

    public ChatRoom() {
    }

    public ChatRoom(String users, String admin, String status, String avatar, String type, String name, String nikname) {
        this.users = users;
        this.admin = admin;
        this.status = status;
        this.avatar = avatar;
        this.type = type;
        this.name = name;
        this.nikname = nikname;
    }
}
