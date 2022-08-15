package com.example.mymessenger.obj;

public class Messenger {
    public String status;
    public String sender;
    public String mess;
    public String type;
    public String time;
    public String day;

    public Messenger() {
    }

    public Messenger(String status, String sender, String mess, String type, String time, String day) {
        this.status = status;
        this.sender = sender;
        this.mess = mess;
        this.type = type;
        this.time = time;
        this.day = day;
    }
}
