package MainFragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dutchpengdemo.MainActivity;
import com.example.dutchpengdemo.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import MainRecyclerView.UserGroupViewAdapter;
import MainRecyclerView.UserGroupViewData;

public class MainDutchpengFragment extends Fragment {

    private final String HOME_TAG = "MAIN_HOME_TAG";
    FirebaseFirestore user_db;

    private MainActivity mainActivity;
    private ArrayList<String> userGroupUids; //유저가 속해있는 그룹의 uid 가 저장되어 있는 곳.
    private ArrayList<UserGroupViewData> userGroupDataList;

    // 그룹이 없을시에 보여지는 뷰.
    private TextView text_no_group;
    private ImageView img_no_group;
    private RecyclerView group_recy_viewer;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frag_main_dutchpeng, container, false);

        initParams();
        initView(view);

        Log.w(mainActivity.LIFE_CYCLE_TAG, "On Create View!");

        if (userGroupUids != null && !userGroupUids.isEmpty()) {
            new GroupDataAsyncTask().execute();
        }


        return view;

    }


    private void initParams() {

        mainActivity = (MainActivity) getActivity();
        userGroupDataList = new ArrayList<>();

        user_db = FirebaseFirestore.getInstance();

        Bundle received = getArguments();

        if (received != null) {
            userGroupUids = received.getStringArrayList("userGroups");
        }

    }


    private void initView(View view) {

        img_no_group = view.findViewById(R.id.img_no_group);
        text_no_group = view.findViewById(R.id.text_no_group);
        group_recy_viewer = view.findViewById(R.id.main_group_recy_viewer);

        if (userGroupUids == null || userGroupUids.isEmpty()) {
            img_no_group.setVisibility(View.VISIBLE);
            text_no_group.setVisibility(View.VISIBLE);

        } else {
            img_no_group.setVisibility(View.GONE);
            text_no_group.setVisibility(View.GONE);
        }

    }


    // 그룹 데이터 어레이를 만들기 위해 들어오는 곳.
    private void makeGroupDataArray() {

        for (int i = 0; i < userGroupUids.size(); i++) {

            getGroupInfo(userGroupUids.get(i));

        }

        Log.w(HOME_TAG, "Array Read:" + userGroupDataList.get(0).getGroupName());

    }

    // 각 그룹의 정보들을 가져와서 데이터화 시켜서 Array 로 바인딩 해줌.
    private void getGroupInfo(String group_uid) {

        user_db.collection("Groups").document(group_uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {

                    // 추가적인 그룹 정보 여기서 해결. ***** // Array 만드는 곳.
                    Map<String, Object> group_data_map = documentSnapshot.getData();

                    String groupName = (String) group_data_map.get("groupName");

                    UserGroupViewData groupInstance = new UserGroupViewData(groupName);

                    userGroupDataList.add(groupInstance);


                } else {// 해당 그룹 부재.

                    Toast.makeText(mainActivity.getApplicationContext(), "해당 그룹이 존재하지 않습니다.", Toast.LENGTH_SHORT).show();

                    Log.e(HOME_TAG, "The group with uid [" + group_uid + "] doesn't exist.");

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                // 시스템 적인 문제로 불러오기 실패.
                Toast.makeText(mainActivity.getApplicationContext(), "유저 그룹 불러오기 실패.", Toast.LENGTH_SHORT).show();

                Log.e(HOME_TAG, "ERROR:: " + e.getMessage());

            }
        });

    }


    public class GroupDataAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            // 로딩 다이얼로그 주기.

        }

        @Override
        protected Void doInBackground(Void... voids) {
            // 데이터들 준비.

            makeGroupDataArray();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // 다이얼로그 해제.

            initiateRecyclerView();
        }

    }

    private void initiateRecyclerView() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mainActivity);
        group_recy_viewer.setLayoutManager(linearLayoutManager);

        UserGroupViewAdapter recy_adapter = new UserGroupViewAdapter(mainActivity.getApplicationContext(), userGroupDataList);
        group_recy_viewer.setAdapter(recy_adapter);

    }


    @Override
    public void onStart() {
        super.onStart();

        Log.w(mainActivity.LIFE_CYCLE_TAG, "Fragment: on Start!");

    }

    @Override
    public void onResume() {
        super.onResume();


        Log.w(mainActivity.LIFE_CYCLE_TAG, "Fragment: on Resume!");

    }

    @Override
    public void onPause() {
        super.onPause();

        Log.w(mainActivity.LIFE_CYCLE_TAG, "Fragment: on Pause!");

    }

    @Override
    public void onStop() {
        super.onStop();

        Log.w(mainActivity.LIFE_CYCLE_TAG, "Fragment: On Stop!");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Log.w(mainActivity.LIFE_CYCLE_TAG, "Fragment: On Destroy View!");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.w(mainActivity.LIFE_CYCLE_TAG, "On Destroy!");
    }
}
