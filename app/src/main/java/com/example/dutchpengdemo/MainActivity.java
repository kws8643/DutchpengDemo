package com.example.dutchpengdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private final String MAIN_TAG = "MAINACTIVITY LOG";

    private FirebaseAuth mAuth;
    private FirebaseUser dUser;
    private FirebaseFirestore user_db = FirebaseFirestore.getInstance();

    public DutchpengUser dutchpengsUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initParams();

        readUserDatabase();
    }

    public void initParams() {

        mAuth = FirebaseAuth.getInstance();
        dUser = mAuth.getCurrentUser();

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

        dutchpengsUser = new DutchpengUser(userUid, userName, userEmail, userPhone);

        Gson gson = new Gson();
        String json = gson.toJson(dutchpengsUser);
        Log.d(MAIN_TAG, json);

        Toast.makeText(getApplicationContext(), "안녕하세요, " + userName + " 회원님.", Toast.LENGTH_SHORT).show();

    }

}
