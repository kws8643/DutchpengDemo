package LoginFragments;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.dutchpengdemo.DutchpengUser;
import com.example.dutchpengdemo.LoginActivity;
import com.example.dutchpengdemo.MainActivity;
import com.example.dutchpengdemo.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kakao.sdk.auth.LoginClient;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton;

import org.json.JSONObject;
import org.w3c.dom.Document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import AppNetworking.NaverOAuthClient;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class LoginFragment extends Fragment {

    public final String OAUTH_LOGIN_TAG = "OAUTH_LOGIN";

    LoginActivity activity;

    private ConstraintLayout btnEmail, btnNaver, btnKakao;
    private Button btnMain;

    //네이버 OAuth params
    private OAuthLoginButton naverConnect;
    private OAuthLogin mNaverSession;
    private OAuthLoginHandler mOAuthLoginHandler;

    //카카오 OAuth params


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frag_login, container, false);

        initParams();

        initView(view);


        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                activity.changeFragment(LoginActivity.FRAGMENT_EMAIL_INDEX);

            }
        });

        btnNaver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                initNaverSession();

                initNaverOAuthHandler();

                naverConnect.setOAuthLoginHandler(mOAuthLoginHandler);

                naverConnect.performClick();
            }
        });

        btnKakao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                performKakaoLogin();

            }
        });

        btnMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent toMain = new Intent(activity.getApplicationContext(), MainActivity.class);

                startActivity(toMain);

                activity.finish();
            }
        });


        return view;

    }

    public void initParams() {
        activity = (LoginActivity) getActivity();
    }

    public void initView(View view) {

        btnEmail = view.findViewById(R.id.btnEmail);
        btnNaver = view.findViewById(R.id.btnNaver);
        btnKakao = view.findViewById(R.id.btnKakao);

        naverConnect = view.findViewById(R.id.naverConnect);

        // 추후에 없앨 버튼.
        btnMain = view.findViewById(R.id.btnMain);
    }

    //--------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------Naver OAuth
    //--------------------------------------------------------------------------------------------

    private void initNaverSession() {

        mNaverSession = OAuthLogin.getInstance();

        mNaverSession.init(activity.getApplicationContext()
                , NaverOAuthClient.getNaverOauthClientId()
                , NaverOAuthClient.getNaverOauthClientSecret()
                , NaverOAuthClient.getNaverOauthClientName());

    }

    // performNaverLogin
    private void initNaverOAuthHandler() {

        mOAuthLoginHandler = new OAuthLoginHandler() {
            @Override
            public void run(boolean success) {
                if (success) {

                    String accessToken = mNaverSession.getAccessToken(activity.getApplicationContext());

                    Log.w(OAUTH_LOGIN_TAG, accessToken);

                    firebaseAuthentication("NAVER", accessToken);

                    return;

                } else {

                    String errorDesc = mNaverSession.getLastErrorDesc(activity.getApplicationContext());

                    Log.e(OAUTH_LOGIN_TAG, "네이버 로그인 실패 :: " + errorDesc);

                    return;
                }
            }
        };
    }


    //--------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------Kakao OAuth
    //--------------------------------------------------------------------------------------------


    private void performKakaoLogin() {


        LoginClient.getInstance().loginWithKakaoAccount(activity.getApplicationContext(), new Function2<OAuthToken, Throwable, Unit>() {
            @Override
            public Unit invoke(OAuthToken token, Throwable error) { //OAuthToken 객체: 액토, 리토, 각각 만료시간의 정보를 담는 객체.
                if (error != null) {

                    Log.e(OAUTH_LOGIN_TAG, "카카오 로그인 실패 :: ", error);

                } else {

                    String accessToken = token.getAccessToken();

                    firebaseAuthentication("KAKAO", accessToken);

                }
                return null;
            }
        });
    }


    // 모든 OAuth 들이 최종적으로 오는 곳.
    private void firebaseAuthentication(String oauth_provider, String accessToken){

        getFirebaseJwt(oauth_provider, accessToken).continueWithTask(new Continuation<String, Task<AuthResult>>() {
            @Override
            public Task<AuthResult> then(@NonNull Task<String> task) throws Exception {

                String firebaseToken = task.getResult();

                FirebaseAuth mAuth = FirebaseAuth.getInstance();

                return mAuth.signInWithCustomToken(firebaseToken);
            }

        }).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    // 일단 여기까지 온거가 OAuth user 라는 것은 자명.
                    //Firebase db 가 있는지로 판단.
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    String user_uid = mAuth.getUid();

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference docRefCheck = db.collection("Users").document(user_uid);

                    docRefCheck.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if(task.isSuccessful()){

                                DocumentSnapshot document = task.getResult();

                                if(document.exists()){

                                    // 있으면 회원가입 쭉 진행한 객체. 확인이 되었으니 바로 로그인 시켜준다.
                                    activity.login();

                                    Log.w(OAUTH_LOGIN_TAG, "USER Logging in ID:: " + user_uid);

                                }else{

                                    // 없으면 회원가입 미진행 객체.
                                    activity.changeFragment(LoginActivity.FRAGMENT_OAUTH_SIGNIN_INDEX);

                                    Log.w(OAUTH_LOGIN_TAG, "First User :: " + user_uid);

                                }

                            }else{// 그냥 조회하는 과정이 실패.

                                Log.e(OAUTH_LOGIN_TAG,"Failed to check :: ", task.getException());

                            }
                        }
                    });



                }else{

                    Toast.makeText(activity.getApplicationContext(), "마지막에서 파베 토큰 활용 실패!", Toast.LENGTH_SHORT).show();

                    if(task.getException() != null){
                        Log.e(OAUTH_LOGIN_TAG, task.getException().toString());

                    }
                }
            }
        });

    }


    private Task<String> getFirebaseJwt(String oauth_provider, final String accessToken) {

        final TaskCompletionSource<String> source = new TaskCompletionSource<>();

        String url;

        if(oauth_provider == "KAKAO"){
            url = getResources().getString(R.string.kakao_validation_server_domain) + "/verifyToken";
        }else if(oauth_provider == "NAVER"){
            url = getResources().getString(R.string.naver_validation_server_domain) + "/verifyToken";
        }else{ // 현재는 이럴 일은 없음.
            url = null;
        }

        RequestQueue queue = Volley.newRequestQueue(activity.getApplicationContext());

        HashMap<String, String> validationObject = new HashMap<>();
        validationObject.put("token", accessToken);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(validationObject), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    Log.w(OAUTH_LOGIN_TAG, response.toString());

                    String firebaseToken = response.getString("firebase_token");

                    source.setResult(firebaseToken);


                } catch (Exception e) {

                    source.setException(e);

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e(OAUTH_LOGIN_TAG, error.getMessage()+ " ");

                source.setException(error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", accessToken);
                return params;
            }
        };

        queue.add(request);

        return source.getTask();
    }
}
