package com.example.dutchpengdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddGroupPage extends AppCompatActivity {

    private final String ADD_GROUP_TAG = "ADD_GROUP_TAG";
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore user_db;

    private EditText input_group_name;
    private Button btnCreateGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group_page);

        initParams();

        initView();

        btnCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!input_group_name.getText().toString().equals("")){
                    createGroup();
                }else{
                    Toast.makeText(getApplicationContext(), "그룹명을 입력해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void initParams(){

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        user_db = FirebaseFirestore.getInstance();

    }

    private void initView(){

        input_group_name = findViewById(R.id.input_group_name);
        btnCreateGroup = findViewById(R.id.btnCreateGroup);

    }

    private void createGroup() {


        Map<String, Object> group_info = new HashMap<>();

        String groupName = input_group_name.getText().toString();

        ArrayList<String> groupMembers = new ArrayList<>();
        groupMembers.add(mUser.getUid());

        //그리고 그룹 사진도 있음.

        group_info.put("groupName", groupName);
        group_info.put("groupMembers", groupMembers);


        user_db.collection("Groups").add(group_info).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {

                String created_group_doc_id = documentReference.getId();

                Log.d(ADD_GROUP_TAG, "Saved Doc ID: " + created_group_doc_id);

                if (!created_group_doc_id.equals("")) {

                    DocumentReference user_doc_ref = user_db.collection("Users").document(mUser.getUid());

                    user_doc_ref.update("userGroups", FieldValue.arrayUnion(created_group_doc_id));

                } else {

                    Toast.makeText(getApplicationContext(), "유저 그룹 추가 실패", Toast.LENGTH_SHORT).show();

                }

                finish();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(getApplicationContext(), "그룹 형성에 실패하였습니다.", Toast.LENGTH_SHORT).show();

                Log.w(ADD_GROUP_TAG, "Error adding document", e);

            }
        });
    }

}