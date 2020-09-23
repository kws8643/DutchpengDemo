package LoginFragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.dutchpengdemo.LoginActivity;
import com.example.dutchpengdemo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginEmailFragment extends Fragment {

    LoginActivity activity;
    public static final String LOGIN_EMAIL_TAG = "LOGIN_EMAIL";

    private FirebaseAuth mAuth;

    private EditText input_email, input_pw;
    private Button btnConfirmEmail;
    private TextView signin_text;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_login_email, container, false);

        initParams();
        initView(view);

        btnConfirmEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmUser();
            }
        });

        signin_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                activity.changeFragment(LoginActivity.FRAGMENT_EMAIL_SIGNIN_INDEX);

            }
        });

        return view;

    }

    public void initParams() {

        activity = (LoginActivity) getActivity();
        mAuth = FirebaseAuth.getInstance();

    }

    public void initView(View view) {

        input_email = view.findViewById(R.id.input_email);
        input_pw = view.findViewById(R.id.input_pw);
        btnConfirmEmail = view.findViewById(R.id.btnConfirmEmail);
        signin_text = view.findViewById(R.id.signin_text1);

    }


    private void confirmUser() {

        final String userEmail = input_email.getText().toString();
        final String userPw = input_pw.getText().toString();

        // 이메일로 로그인 시도.
        mAuth.signInWithEmailAndPassword(userEmail, userPw).addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    Log.d(LOGIN_EMAIL_TAG, "loginWithEmail: success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    // 이후 진행.

                    activity.login();

                } else { // 없음을 확인, 회원가입 진행.

                    Log.w(LOGIN_EMAIL_TAG, "loginWithEmail: failure");
                    Toast.makeText(activity.getApplicationContext(), "아이디 / 비번 오류", Toast.LENGTH_SHORT).show();

                }

            }
        });
    }
}
