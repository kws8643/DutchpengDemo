package LoginFragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.dutchpengdemo.LoginActivity;
import com.example.dutchpengdemo.R;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton;

import AppNetworking.NaverOAuthClient;

public class LoginFragment extends Fragment {

    LoginActivity activity;


    private ConstraintLayout btnEmail, btnNaver;

    //네이버 OAuth params
    private OAuthLoginButton naverConnect;
    private OAuthLogin mNaverSession;
    private OAuthLoginHandler mOAuthLoginHandler;


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

        return view;

    }

    public void initParams() {
        activity = (LoginActivity) getActivity();
    }

    public void initView(View view) {

        btnEmail = view.findViewById(R.id.btnEmail);
        btnNaver = view.findViewById(R.id.btnNaver);

        naverConnect = view.findViewById(R.id.naverConnect);
    }

    //--------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------Naver OAuth
    //--------------------------------------------------------------------------------------------

    private void initNaverSession(){

        mNaverSession = OAuthLogin.getInstance();

        mNaverSession.init(activity.getApplicationContext()
                , NaverOAuthClient.getNaverOauthClientId()
                , NaverOAuthClient.getNaverOauthClientSecret()
                , NaverOAuthClient.getNaverOauthClientName());

    }

    private void initNaverOAuthHandler(){

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


    private class RequestNaverAPI extends AsyncTask<Void, Void, String>{

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


}
