package com.example.mymessenger.obj;

public class Avatar {
    public String key_user;
    public String urla;
    public String time;
    public String status;
    public String avt_now;

    public Avatar() {
    }

    public Avatar(String key_user, String urla, String time, String status, String avt_now) {
        this.key_user = key_user;
        this.urla = urla;
        this.time = time;
        this.status = status;
        this.avt_now = avt_now;
    }
}
