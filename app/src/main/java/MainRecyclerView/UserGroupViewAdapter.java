package MainRecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dutchpengdemo.R;

import java.util.ArrayList;

public class UserGroupViewAdapter extends RecyclerView.Adapter<UserGroupViewAdapter.UserGroupViewHolder> {

    private Context activityContext;
    private ArrayList<UserGroupViewData> userGroupData;

    //Custom OnItemClickListener 도 적용해야함.

    public UserGroupViewAdapter(Context activityContext, ArrayList<UserGroupViewData> userGroupData){

        this.activityContext = activityContext;
        this.userGroupData = userGroupData;

    }

    @NonNull
    @Override
    public UserGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(activityContext).inflate(R.layout.view_main_user_group_recy_item, parent,false);

        // 해당 그룹의 뷰어 정보가 존재하는 Holder.
        UserGroupViewHolder userGroupHolder = new UserGroupViewHolder(view);

        return userGroupHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserGroupViewHolder holder, int position) {

        // 받은 유저의 그룹 데이터들을 받아왔을 때 한 그룹의 데이터.
        UserGroupViewData groupData = userGroupData.get(position);


        holder.groupName.setText(groupData.getGroupName());

    }

    @Override
    public int getItemCount() {
        return userGroupData.size();
    }




    public class UserGroupViewHolder extends RecyclerView.ViewHolder {

        public ImageView groupImage;
        public TextView groupName, groupRecentAct;

        public UserGroupViewHolder(@NonNull View itemView) {
            super(itemView);

            groupImage = itemView.findViewById(R.id.group_img);
            groupName = itemView.findViewById(R.id.text_group_name);
            groupRecentAct = itemView.findViewById(R.id.text_group_recent_act);


        }
    }

}
