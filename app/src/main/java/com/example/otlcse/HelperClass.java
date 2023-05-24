package com.example.otlcse;

public class HelperClass {
    String firstname, lastname, phone, nid, email, username, password, key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getFirstname() {
        return firstname;
    }


    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNid() {
        return nid;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public HelperClass(String firstname, String lastname, String phone, String nid, String email, String username, String password, String key) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.phone = phone;
        this.nid = nid;
        this.email = email;
        this.username = username;
        this.password = password;
        this.key = key;
    }

    public HelperClass() {
    }
}
