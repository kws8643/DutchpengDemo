package LoginFragments;

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

                    //String accToken;


                    return;

                } else {

                    //String errorCode;
                    //String errorDesc;


                    return;
                }
            }
        };
    }






    //--------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------Kakao OAuth
    //--------------------------------------------------------------------------------------------


}
