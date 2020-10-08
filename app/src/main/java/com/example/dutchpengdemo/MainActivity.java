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

import MainFragments.MainDutchpengFragment;
import MainFragments.MainMoreFragment;
import MainFragments.MainReservationFragment;

public class MainActivity extends AppCompatActivity {

    private final String MAIN_TAG = "MAINACTIVITY LOG";

    private FirebaseAuth mAuth;
    private FirebaseUser dUser;
    private FirebaseFirestore user_db = FirebaseFirestore.getInstance();

    public DutchpengUser dutchpengUser;


    // Fragment var.
    FragmentManager manager;

    MainDutchpengFragment fragDutchpeng;
    MainReservationFragment fragReservation;
    MainMoreFragment fragMore;

    private BottomNavigationView btmNavView;
    private BottomNavigationView.OnNavigationItemSelectedListener btmNavListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            int id = item.getItemId();

            FragmentTransaction transaction = manager.beginTransaction();
            transaction.addToBackStack(null);

            switch (id){
                case R.id.navbtn_dutchpeng:
                    transaction.replace(R.id.main_frame, fragDutchpeng);
                    break;
                case R.id.navbtn_reservation:
                    transaction.replace(R.id.main_frame, fragReservation);
                    break;
                case R.id.navbtn_more:
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

//        readUserDatabase();
    }

    public void initParams() {

        mAuth = FirebaseAuth.getInstance();
        dUser = mAuth.getCurrentUser();

        //Frags
        manager = getSupportFragmentManager();

        fragDutchpeng = new MainDutchpengFragment();
        fragReservation = new MainReservationFragment();
        fragMore = new MainMoreFragment();

    }

    public void initView(){

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

    public void buildUser(Map<String, Object> userMap){

        String userUid = dUser.getUid();
        String userEmail = dUser.getEmail();
        String userName = userMap.get("userName").toString();
        String userPhone = userMap.get("userPhone").toString();

        dutchpengUser = new DutchpengUser(userUid, userName, userEmail, userPhone);

        Gson gson = new Gson();
        String json = gson.toJson(dutchpengUser);
        Log.d(MAIN_TAG, json);

        Toast.makeText(getApplicationContext(), "안녕하세요, " + userName + " 회원님.", Toast.LENGTH_SHORT).show();
    }

}
