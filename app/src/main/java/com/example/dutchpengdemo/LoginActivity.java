package com.example.dutchpengdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

import LoginFragments.LoginEmailFragment;
import LoginFragments.LoginFragment;
import LoginFragments.LoginEmailSigninFragment;

public class LoginActivity extends AppCompatActivity {

    public static final int FRAGMENT_EMAIL_INDEX = 1;
    public static final int FRAGMENT_EMAIL_SIGNIN_INDEX = 2;

    FragmentManager manager;
    LoginFragment frag_login;
    LoginEmailFragment frag_email_login;
    LoginEmailSigninFragment frag_email_signin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        manager = getSupportFragmentManager();

        frag_login = new LoginFragment();
        frag_email_login = new LoginEmailFragment();
        frag_email_signin = new LoginEmailSigninFragment();

        manager.beginTransaction().add(R.id.frame_login, frag_login).commit();

    }

    public void changeFragment(int index) {

        FragmentTransaction transaction = manager.beginTransaction();

        transaction.addToBackStack(null);

        if (index == FRAGMENT_EMAIL_INDEX) {

            transaction.replace(R.id.frame_login, frag_email_login).commit();

        } else if (index == FRAGMENT_EMAIL_SIGNIN_INDEX) {

            transaction.replace(R.id.frame_login, frag_email_signin).commit();
        }

    }

    public void login(){

        Intent login_intent = new Intent(this, MainActivity.class);

        startActivity(login_intent);

        finish();
    }
/*
    private void getAppKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Log.e("Hash key", something);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("name not found", e.toString());
        }
    }
*/


}
