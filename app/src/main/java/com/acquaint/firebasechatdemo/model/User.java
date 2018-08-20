package com.acquaint.firebasechatdemo.model;

/**
 * Created by acquaint on 26/7/18.
 */

public class User {
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    String fname;
    String lname;
    String email;
    String gender;
    String phone;
    String token;

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public User(String fname, String lname, String email, String gender, String phone) {
        this.fname = fname;
        this.lname = lname;
        this.email = email;
        this.gender = gender;
        this.phone = phone;

    }

    public User() {
    }

}
