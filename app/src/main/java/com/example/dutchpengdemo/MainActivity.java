package com.example.dutchpengdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
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

import java.util.Map;

import AppTools.BackPressedHandler;
import MainFragments.MainDutchpengFragment;
import MainFragments.MainMoreFragment;
import MainFragments.MainReservationFragment;

public class MainActivity extends AppCompatActivity {

    private final String MAIN_TAG = "MAIN_ACTIVITY LOG";

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

    private Bundle frag_data_bundle = new Bundle();
    private BottomNavigationView btmNavView;
    private BottomNavigationView.OnNavigationItemSelectedListener btmNavListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            int id = item.getItemId();

            FragmentTransaction transaction = manager.beginTransaction();

            switch (id) {

                case R.id.navbtn_dutchpeng:

                    transaction.replace(R.id.main_frame, fragDutchpeng);
                    break;

                case R.id.navbtn_reservation:

                    transaction.replace(R.id.main_frame, fragReservation);
                    break;

                case R.id.navbtn_more: // 유저 프로필 정보 파싱 및 넘겨줘야 함.

                    String userProvider = dutchpengUser.getProvider();
                    String userEmail = dutchpengUser.getUserEmail();
                    String userName = dutchpengUser.getUserName();
                    String userPhotoUri = dutchpengUser.getUserPhotoUri();

                    frag_data_bundle.putString("userProvider", userProvider);
                    frag_data_bundle.putString("userName", userName);
                    frag_data_bundle.putString("userEmail", userEmail);
                    frag_data_bundle.putString("userPhotoUri", userPhotoUri);

                    Log.w("hey", userEmail + ", " + userName + ", " + userPhotoUri);

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

        initParams();
        initView();

        btmNavView.setOnNavigationItemSelectedListener(btmNavListener);

        manager.beginTransaction().add(R.id.main_frame, fragDutchpeng).commit();
        btmNavView.setSelectedItemId(R.id.navbtn_dutchpeng);

        readUserDatabase();
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

    }

    public void initView() {

        btmNavView = findViewById(R.id.main_navigation);

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

        if (userUid.substring(0,5).equals("kakao")) {

            provider = "KAKAO";

        } else if (userUid.substring(0,5).equals("naver")) {

            provider = "NAVER";

        } else {

            provider = "EMAIL";

        }

        String userEmail = dUser.getEmail();
        String userName = userMap.get("userName").toString();
        String userPhone = userMap.get("userPhone").toString();
        String userPhotoUri = null;

        if (dUser.getPhotoUrl() != null) {
            userPhotoUri = dUser.getPhotoUrl().toString();
        }
//        String userPhotoUri = "not yet";

        dutchpengUser = new DutchpengUser(provider, userUid, userName, userEmail, userPhone, userPhotoUri);

        Gson gson = new Gson();
        String json = gson.toJson(dutchpengUser);
        Log.d(MAIN_TAG, json);

        Toast.makeText(getApplicationContext(), "안녕하세요, " + userName + " 회원님.", Toast.LENGTH_SHORT).show();

    }

    // back 버튼 눌렀을 때 한번 더 누르면 종료하는 메소드
    @Override
    public void onBackPressed() {

        backPressedHandler.onBackPressed();

    }

}
