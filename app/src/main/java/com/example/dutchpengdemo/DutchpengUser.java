package com.example.dutchpengdemo;

public class DutchpengUser {

    private String userUid;
    private String userName;
    private String userEmail;
    private String userPhone;

    public DutchpengUser(String userUid, String userName, String userEmail, String userPhone){

        this.userUid = userUid;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPhone = userPhone;
    }


    public String getUserUid() {
        return userUid;
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
}
