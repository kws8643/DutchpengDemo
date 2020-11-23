package com.example.dutchpengdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.LogWriter;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Map;

import AppTools.BackPressedHandler;
import MainFragments.MainDutchpengFragment;
import MainFragments.MainMoreFragment;
import MainFragments.MainReservationFragment;

public class MainActivity extends AppCompatActivity {

    private final String MAIN_TAG = "MAIN_ACTIVITY LOG";
    public final String LIFE_CYCLE_TAG = "LIFECYCLE";

    // User var
    private FirebaseAuth mAuth;
    private FirebaseUser dUser;
    private FirebaseFirestore user_db = FirebaseFirestore.getInstance();

    public DutchpengUser dutchpengUser;

    // Activity Handlers
    private BackPressedHandler backPressedHandler;


    // Fragment var
    FragmentManager manager;

    MainDutchpengFragment fragDutchpeng;
    MainReservationFragment fragReservation;
    MainMoreFragment fragMore;


    public ImageView btnNotification, btnAddGroup;

    private BottomNavigationView btmNavView;
    private Bundle frag_data_bundle = new Bundle();
    private BottomNavigationView.OnNavigationItemSelectedListener btmNavListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            int id = item.getItemId();

            frag_data_bundle.clear();

            FragmentTransaction transaction = manager.beginTransaction();

            switch (id) {

                case R.id.navbtn_dutchpeng:

                    btnAddGroup.setVisibility(View.VISIBLE);
                    btnNotification.setVisibility(View.VISIBLE);

                    if(dutchpengUser.getUserGroups() != null) {
                        frag_data_bundle.putStringArrayList("userGroups", dutchpengUser.getUserGroups());
                        fragDutchpeng.setArguments(frag_data_bundle);
                    }

                    transaction.replace(R.id.main_frame, fragDutchpeng);
                    break;

                case R.id.navbtn_reservation:

                    btnAddGroup.setVisibility(View.GONE);
                    btnNotification.setVisibility(View.GONE);

                    transaction.replace(R.id.main_frame, fragReservation);
                    break;

                case R.id.navbtn_more: // 유저 프로필 정보 파싱 및 넘겨줘야 함.

                    btnAddGroup.setVisibility(View.GONE);
                    btnNotification.setVisibility(View.VISIBLE);

                    frag_data_bundle.putString("userProvider", dutchpengUser.getProvider());
                    frag_data_bundle.putString("userName", dutchpengUser.getUserEmail());
                    frag_data_bundle.putString("userEmail", dutchpengUser.getUserName());

                    fragMore.setArguments(frag_data_bundle);

                    transaction.replace(R.id.main_frame, fragMore);
                    break;

            }

            transaction.commit();

            return true;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.w(LIFE_CYCLE_TAG, "On Create!");

        initParams();
        initView();

        btmNavView.setOnNavigationItemSelectedListener(btmNavListener);

        readUserDatabase(); // 처음에 화면이 띄워질 때.

        btnAddGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent add_group_activity = new Intent(getApplicationContext(), AddGroupPage.class);
                startActivity(add_group_activity);
            }
        });


        btnNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "알림 내역 확인", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void initParams() {

        //User var
        mAuth = FirebaseAuth.getInstance();
        dUser = mAuth.getCurrentUser();

        //Activity var
        backPressedHandler = new BackPressedHandler(this);

        //Frags
        manager = getSupportFragmentManager();

        fragDutchpeng = new MainDutchpengFragment();
        fragReservation = new MainReservationFragment();
        fragMore = new MainMoreFragment();

        //
        btnNotification = findViewById(R.id.btnNotification);
        btnAddGroup = findViewById(R.id.btnAddGroup);


    }

    public void initView() {

        btmNavView = findViewById(R.id.main_navigation);

        btnAddGroup = findViewById(R.id.btnAddGroup);
        btnNotification = findViewById(R.id.btnNotification);


    }

    private void readUserDatabase() {

        DocumentReference docRef = user_db.collection("Users").document(dUser.getUid());

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(MAIN_TAG, "DocumentSnapshot data: " + document.getData());

                        Map<String, Object> userDataMap = document.getData();

                        buildUser(userDataMap);

                    } else {
                        Log.d(MAIN_TAG, "No such document");
                    }

                } else {
                    Log.w(MAIN_TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    public void buildUser(Map<String, Object> userMap) {

        String provider;
        String userUid = dUser.getUid();

        if (userUid.substring(0, 5).equals("kakao")) {

            provider = "KAKAO";

        } else if (userUid.substring(0, 5).equals("naver")) {

            provider = "NAVER";

        } else {

            provider = "EMAIL";

        }

        String userEmail = dUser.getEmail();
        String userName = userMap.get("userName").toString();
        String userPhone = userMap.get("userPhone").toString();

        ArrayList<String> userGroups = (ArrayList<String>) userMap.get("userGroups");

        Log.w(MAIN_TAG, userEmail + "," + userName);

        dutchpengUser = new DutchpengUser(provider, userUid, userName, userEmail, userPhone, userGroups);

        Gson gson = new Gson();
        String json = gson.toJson(dutchpengUser);
        Log.d(MAIN_TAG, json);

        Toast.makeText(getApplicationContext(), "안녕하세요, " + userName + " 회원님.", Toast.LENGTH_SHORT).show();

        // 첫 유저 형성. Listener 의 맨 마지막에!
        btmNavView.setSelectedItemId(R.id.navbtn_dutchpeng);
    }

    // back 버튼 눌렀을 때 한번 더 누르면 종료하는 메소드
    @Override
    public void onBackPressed() {

        backPressedHandler.onBackPressed();

    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.w(LIFE_CYCLE_TAG, "On Start!");
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.w(LIFE_CYCLE_TAG, "On Stop!");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.w(LIFE_CYCLE_TAG, "On Destroy!");
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.w(LIFE_CYCLE_TAG, "On Pause!");
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.w(LIFE_CYCLE_TAG, "On Resume!");
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();

        Log.w(LIFE_CYCLE_TAG, "On Resume Fragments!!");

    }


}


