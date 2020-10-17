package LoginFragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.dutchpengdemo.LoginActivity;
import com.example.dutchpengdemo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginEmailSigninFragment extends Fragment {

    private final String EMAIL_SIGNIN_TAG = "EMAIL_SIGNIN_TAG";

    LoginActivity activity;
    FirebaseAuth mAuth;
    FirebaseFirestore email_db;

    EditText input_email, input_pw, input_phone, input_name;
    Button btnSignin;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frag_login_signin_email, container, false);

        initParams();
        initView(view);


        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setUserDatabase();
            }
        });

        return view;

    }

    public void initParams() {

        activity = (LoginActivity) getActivity();

        mAuth = FirebaseAuth.getInstance();
        email_db = FirebaseFirestore.getInstance();

    }

    public void initView(View view) {

        input_email = view.findViewById(R.id.input_email);
        input_pw = view.findViewById(R.id.input_pw);

        input_name = view.findViewById(R.id.input_name);
        input_phone = view.findViewById(R.id.input_phone);

        btnSignin = view.findViewById(R.id.btnSignin);
    }


    private void setUserDatabase() {

        String userEmail = input_email.getText().toString();
        String userPw = input_pw.getText().toString();

        mAuth.createUserWithEmailAndPassword(userEmail, userPw).addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    Log.d(EMAIL_SIGNIN_TAG, "User Creation Successful");

                    createUserDatabase();

                } else {

                    Log.w(EMAIL_SIGNIN_TAG, "User Creation Failed:: " + task.getException().getMessage());

                    Toast.makeText(activity.getApplicationContext(), "이메일 가입 실패", Toast.LENGTH_SHORT).show();

                }

            }
        });

    }


    private void createUserDatabase() {

        FirebaseUser c_user = mAuth.getCurrentUser();
        String user_uid = c_user.getUid();

        Map<String, Object> user_info = new HashMap<>();

        user_info.put("userName", input_name.getText().toString());
        user_info.put("userPhone", input_phone.getText().toString());


        email_db.collection("Users").document(user_uid).set(user_info)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(EMAIL_SIGNIN_TAG, "User Info Successfully Added::");

                        activity.login();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(EMAIL_SIGNIN_TAG, "Error while adding user", e);
            }
        });
    }
}
