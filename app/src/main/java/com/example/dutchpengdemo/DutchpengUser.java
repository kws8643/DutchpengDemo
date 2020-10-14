package com.example.dutchpengdemo;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.UserInfo;
import com.kakao.sdk.user.UserApi;

// Dutchpeng User 와 잘 연계되게 녹여야 한다.

public class DutchpengUser implements UserInfo {

    private String userUid;
    private String userName;
    private String userEmail;
    private String userPhone;


    public DutchpengUser(String userUid, String userName, String userEmail, String userPhone) {

        this.userUid = userUid;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPhone = userPhone;
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

    @NonNull
    @Override
    public String getUid() {
        return userUid;
    }

    @NonNull
    @Override
    public String getProviderId() {
        return null;
    }

    @Nullable
    @Override
    public String getDisplayName() {
        return null;
    }

    @Nullable
    @Override
    public Uri getPhotoUrl() {
        return null;
    }

    @Nullable
    @Override
    public String getEmail() {
        return null;
    }

    @Nullable
    @Override
    public String getPhoneNumber() {
        return null;
    }

    @Override
    public boolean isEmailVerified() {
        return false;
    }
}
