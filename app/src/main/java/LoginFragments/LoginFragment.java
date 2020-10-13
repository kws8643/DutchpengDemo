package LoginFragments;

import android.content.Intent;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.dutchpengdemo.LoginActivity;
import com.example.dutchpengdemo.MainActivity;
import com.example.dutchpengdemo.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.kakao.sdk.auth.LoginClient;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton;

import org.json.JSONObject;

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

    private void initNaverOAuthHandler() {

        mOAuthLoginHandler = new OAuthLoginHandler() {
            @Override
            public void run(boolean success) {
                if (success) {


                    // 기존 가입 회원인지 확인 필요 -> 회원이면 MainActivity로 넘어가서 객체 생성.
                    //                        -> 아니라면 naver 연동 완료 후 signin 으로 이동.

//                    new RequestNaverAPI().execute();

                    return;

                } else {

                    //String errorCode;
                    //String errorDesc;


                    return;
                }
            }
        };
    }


    //* 정보 요청은 서버단에서 하는걸로 수정. *//
    private class RequestNaverAPI extends AsyncTask<Void, Void, String> {

        /*@Override
        protected void onPreExecute() {
            super.onPreExecute();
        }*/

        @Override
        protected String doInBackground(Void... voids) {

            String naverRequsetUrl = "https://openapi.naver.com/v1/nid/me";
            String accessToken = mNaverSession.getAccessToken(activity.getApplicationContext());


            return mNaverSession.requestApi(activity.getApplicationContext(), accessToken, naverRequsetUrl);
        }

        @Override
        protected void onPostExecute(String str) {
            super.onPostExecute(str);


        }
    }


    //--------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------Kakao OAuth
    //--------------------------------------------------------------------------------------------


    private void performKakaoLogin() {


        LoginClient.getInstance().loginWithKakaoAccount(activity.getApplicationContext(), new Function2<OAuthToken, Throwable, Unit>() {
            @Override
            public Unit invoke(OAuthToken token, Throwable error) { //OAuthToken 객체: 액토, 리토, 각각 만료시간의 정보를 담는 객체.
                if (error != null) {
                    Log.e(OAUTH_LOGIN_TAG, "로그인 실패: 카카오 액토 반환 실패.", error);

                } else {

                    String accessToken = token.getAccessToken();

                    firebaseAuthentication(accessToken);

                }
                return null;
            }
        });
    }

    private void firebaseAuthentication(String kakaoAccessToken){

        getFirebaseJwt(kakaoAccessToken).continueWithTask(new Continuation<String, Task<AuthResult>>() {
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

                    // 성공적으로 Auth 성공.

                }else{

                    Toast.makeText(activity.getApplicationContext(), "마지막에서 파베 토큰 활용 실패!", Toast.LENGTH_SHORT).show();

                    if(task.getException() != null){
                        Log.e(OAUTH_LOGIN_TAG, task.getException().toString());

                    }
                }
            }
        });

    }


    private Task<String> getFirebaseJwt(final String kakaoAccessToken) {

        final TaskCompletionSource<String> source = new TaskCompletionSource<>();

        RequestQueue queue = Volley.newRequestQueue(activity.getApplicationContext());
        String url = getResources().getString(R.string.validation_server_domain) + "/verifyToken";

        HashMap<String, String> validationObject = new HashMap<>();
        validationObject.put("token", kakaoAccessToken);

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

                Log.e(OAUTH_LOGIN_TAG, error.getMessage());

                source.setException(error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", kakaoAccessToken);
                return params;
            }
        };

        queue.add(request);

        return source.getTask();
    }
}
