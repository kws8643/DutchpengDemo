package AppTools;

import android.app.Activity;
import android.widget.Toast;

public class BackPressedHandler {

    private Activity curActivity;
    private long backPressedTime = 0;
    private Toast toast;

    public BackPressedHandler(Activity context){

        this.curActivity = context;
    }

    public void onBackPressed(){
        if(System.currentTimeMillis() > backPressedTime + 2000) {
            backPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }

        if(System.currentTimeMillis() <= backPressedTime + 2000){
            toast.cancel();
            curActivity.finish();
        }
    }


    private void showGuide(){
        toast = Toast.makeText(curActivity.getApplicationContext(), "Back 을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT);

        toast.show();
    }

}
