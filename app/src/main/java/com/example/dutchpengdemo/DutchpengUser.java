package com.example.dutchpengdemo;

import java.util.ArrayList;

// Dutchpeng User 와 잘 연계되게 녹여야 한다.

public class DutchpengUser {

    private String provider;
    private String userUid;
    private String userName;
    private String userEmail;
    private String userPhone;
    private ArrayList<String> userGroups;


    public DutchpengUser(String provider, String userUid, String userName, String userEmail, String userPhone, ArrayList<String> userGroups) {

        this.provider = provider;
        this.userUid = userUid;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPhone = userPhone;
        this.userGroups = userGroups;

    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserUid() {
        return userUid;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public ArrayList<String> getUserGroups() {
        return userGroups;
    }

    public void setUserGroups(ArrayList<String> userGroups) {
        this.userGroups = userGroups;
    }
}
