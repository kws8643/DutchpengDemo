package LoginFragments;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.dutchpengdemo.LoginActivity;
import com.example.dutchpengdemo.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.dutchpengdemo.R.drawable.dutchpeng_default;

public class LoginOAuthSigninFragment extends Fragment {

    public final String OAUTH_SIGNIN_TAG = "OAUTH_SIGNIN";

    LoginActivity activity;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    private FirebaseFirestore oauth_database;

    // UIs
    private CircleImageView input_oauth_profile_img;
    private EditText input_oauth_email, input_oauth_name, input_oauth_phone;
    private Button btnOauthSignin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.frag_login_signin_oauth, container, false);

        initParams();
        initView(view);
        updateUI();

        btnOauthSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setUserDatabase();

            }
        });


        return view;
    }

    private void initParams(){

        activity = (LoginActivity) getActivity();

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        oauth_database = FirebaseFirestore.getInstance();

    }

    private void initView(View view){

        input_oauth_profile_img = view.findViewById(R.id.input_oauth_profile_img);
        input_oauth_profile_img.setImageResource(dutchpeng_default);

        input_oauth_email = view.findViewById(R.id.input_oauth_email);
        input_oauth_name = view.findViewById(R.id.input_oauth_name);
        input_oauth_phone = view.findViewById(R.id.input_oauth_phone);
        btnOauthSignin = view.findViewById(R.id.btnOauthSignin);

    }

    private void updateUI(){

        String oauth_email = mUser.getEmail();
//        Uri oauth_profile_img_uri;

        if(oauth_email != null){

            input_oauth_email.setText(oauth_email);

        }else{

            Log.e(OAUTH_SIGNIN_TAG, "No oauth email. Execution Error.");
//            activity.finish();
        }

        /*
        if(mUser.getPhotoUrl() != null){

            oauth_profile_img_uri = mUser.getPhotoUrl();
            input_oauth_profile_img.setImageURI(oauth_profile_img_uri);

        }else{

            input_oauth_profile_img.setImageResource(dutchpeng_default);

        }*/

    }


    private void setUserDatabase(){

        String user_uid = mAuth.getUid();

        String userName = input_oauth_name.getText().toString();
        String userPhone = input_oauth_phone.getText().toString();

        Map<String, Object> user_info = new HashMap<>();

        user_info.put("userName", userName);
        user_info.put("userPhone", userPhone);

        oauth_database.collection("Users").document(user_uid).set(user_info)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(OAUTH_SIGNIN_TAG, "OAuth User Info Successfully Added::");

                        activity.login();

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(OAUTH_SIGNIN_TAG, "Error while adding oauth user", e);
            }
        });

    }
}
