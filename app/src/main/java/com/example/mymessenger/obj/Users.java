package com.example.mymessenger.obj;

public class Users {
    public String avatar;
    public String fullname;
    public String dateofbirth;
    public String sex;
    public String phonenumber;
    public String email;
    public String password;
    public String address;
    public String position;
    public String time_start;
    public String token;
    public String status;

    public Users() {
    }

    public Users(String avatar, String fullname, String dateofbirth, String sex, String phonenumber, String email, String password, String address, String position, String time_start, String token, String status) {
        this.avatar = avatar;
        this.fullname = fullname;
        this.dateofbirth = dateofbirth;
        this.sex = sex;
        this.phonenumber = phonenumber;
        this.email = email;
        this.password = password;
        this.address = address;
        this.position = position;
        this.time_start = time_start;
        this.token = token;
        this.status = status;
    }
}
