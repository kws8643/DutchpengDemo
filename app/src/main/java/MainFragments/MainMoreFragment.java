package MainFragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.dutchpengdemo.LoginActivity;
import com.example.dutchpengdemo.MainActivity;
import com.example.dutchpengdemo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kakao.sdk.user.UserApiClient;
import com.nhn.android.naverlogin.OAuthLogin;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MainMoreFragment extends Fragment {

    private final String OFF_TAG = "SIGN_LOG_OUT_TAG";


    MainActivity mainActivity;

    private String userName, userEmail, userProvider;

    //회원 정보 Viewer
    private ImageView main_more_info_profile_img;
    private TextView main_more_info_name, main_more_info_email;

    private LinearLayout btnLogout, btnSignout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frag_main_more, container, false);

        initParms();

        initView(view);

        updateUI();

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                userLogout();
            }
        });

        btnSignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                userSignout();
            }
        });

        return view;
    }

    private void initParms(){

        mainActivity = (MainActivity) getActivity();

        Bundle received = getArguments();

        userProvider = received.getString("userProvider");
        userName = received.getString("userName");
        userEmail = received.getString("userEmail");

        //        String userPhotoUri = received.getString("userPhotoUri");

//        Log.w("More Frag: hey", userPhotoUri);



    }

    private void initView(View view){

        // 화면 프로필 사진.
        main_more_info_profile_img = view.findViewById(R.id.main_more_info_profile_img);
        main_more_info_profile_img.setImageResource(R.drawable.dutchpeng_default); // 일단 대기

        // 옆 이름, 이메일.
        main_more_info_name = view.findViewById(R.id.main_more_info_name);
        main_more_info_email = view.findViewById(R.id.main_more_info_email);

        btnLogout = view.findViewById(R.id.btnLogout);
        btnSignout = view.findViewById(R.id.btnSignout);


    }

    private void updateUI(){


        main_more_info_name.setText(userName + " 님");
        main_more_info_email.setText(userEmail);

/*        if(userPhotoUri != null) {
            main_more_info_profile_img.setImageURI(Uri.parse(userPhotoUri));
        }else{
            main_more_info_profile_img.setImageResource(R.drawable.dutchpeng_default);
        }
*/

    }


//    ========================================= Button Listeners.

    // 되긴 된다..!
    // 뭔가 맞는지 검증이 필요할 듯.
    private void userLogout(){

        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);

        builder.setTitle("로그아웃");
        builder.setMessage("로그아웃 하시겠어요?");
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                // 오스 아웃.
                if(userProvider == "KAKAO"){
                    UserApiClient.getInstance().logout(new Function1<Throwable, Unit>() {
                        @Override
                        public Unit invoke(Throwable error) {
                            if(error != null){

                                Log.e(OFF_TAG,"KAKAO Log out failed:: ", error);

                            }else{

                                Log.d(OFF_TAG, "KAKAO Log out success!");

                            }

                            return null;
                        }
                    });

                }else  if(userProvider == "NAVER"){

                    OAuthLogin mNaverSession = OAuthLogin.getInstance();
                    mNaverSession.logout(mainActivity.getApplicationContext());


                }

                // 파베 아웃.
                FirebaseAuth.getInstance().signOut(); // 음..

                Intent intent = new Intent(mainActivity.getApplicationContext(), LoginActivity.class);

//                intent.putExtra("data", "1");

                startActivity(intent);

                mainActivity.finish();
            }
        });

        builder.setNegativeButton("아니오", null);

        AlertDialog signOutDialog = builder.create();

        signOutDialog.show();


    }


    private void userSignout(){

        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);

        builder.setTitle("정말로 탈퇴하시나요 ... ?");
        builder.setMessage("회원 탈퇴를 하시면 계정 탈퇴 및 연결된 서비스와의 연동도 해제되어 모든 데이터가 삭제됩니다. 정말 탈퇴하시겠습니까?");
        builder.setPositiveButton("탈퇴", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                // 오스 삭제.
                if(userProvider == "KAKAO"){

                    UserApiClient.getInstance().unlink(new Function1<Throwable, Unit>() {
                        @Override
                        public Unit invoke(Throwable error) {

                            if(error != null){

                                Log.e(OFF_TAG,"KAKAO Sign out failed:: ", error);

                            }else{

                                Log.d(OFF_TAG, "KAKAO Sign out success!");

                            }

                            return null;

                        }
                    });

                }else  if(userProvider == "NAVER"){ // 데이터들 처리는 되는데 연동해제가 안되어서 다시 로그인 시 동의 창이 안뜸.

                    OAuthLogin mNaverSession = OAuthLogin.getInstance();

                    mNaverSession.refreshAccessToken(mainActivity.getApplicationContext());

                    mNaverSession.logoutAndDeleteToken(mainActivity.getApplicationContext());

                }

                // 파베 삭제.
                FirebaseAuth mAuth = FirebaseAuth.getInstance();

                deleteFirebaseDatabase(mAuth);

                mAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){

                            Log.w(OFF_TAG, "Firebase Sign out Successful::");

                            Intent intent = new Intent(mainActivity.getApplicationContext(), LoginActivity.class);

                            startActivity(intent);

                            mainActivity.finish();

                        }else{

                            Log.e(OFF_TAG,"Firebase Sign out failed:: " + task.getException().getMessage());

                        }

                    }
                });


            }
        });

        builder.setNegativeButton("취소", null);

        builder.create().show();

    }


    private void deleteFirebaseDatabase(FirebaseAuth mAuth){

        String delete_uid = mAuth.getUid();

        FirebaseFirestore delete_db = FirebaseFirestore.getInstance();

        delete_db.collection("Users").document(delete_uid).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){

                    Log.w(OFF_TAG, "The user's data has been deleted!");

                }else{

                    Log.e(OFF_TAG,"Data delete failed" + task.getException().getMessage());


                }
            }
        });


    }

}
