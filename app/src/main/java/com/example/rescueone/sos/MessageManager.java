package com.example.rescueone.sos;

import android.content.Context;
import android.telephony.SmsManager;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rescueone.db_phone.DBService;
import com.example.rescueone.db_phone.PreferenceManager;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class MessageManager extends AppCompatActivity {

    private Context mContext;
    private String userName;
    private SmsManager smsManager;

    /*생성자*/
    public MessageManager(Context context){
        mContext = context;
        userName = PreferenceManager.getString(mContext, "name");
        smsManager = SmsManager.getDefault();
    }

    public void sendLink(String phone){
        String text = "[Rescue ONE]" + userName +"님께서 알림 수신자로 지정했습니다. 앱 설치 후 로그인해주세요.";
        smsManager.sendTextMessage(phone,null,text,null,null);
    }

    public void sendSOS() {
        List<String> receivers = null;

        try {
            receivers = getData();
        } catch (ExecutionException e) {
            Log.d("메세지","실패");
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d("메세지","실패");
        }

        GPSManager gm = new GPSManager(mContext);
        //위치정보 획득
        gm.getLocation();
        String location = gm.getAddress();
        String text = "[Rescue ONE]\n" + userName +"님이 긴급한 상황에 처했습니다.";

        if(receivers == null){

        }
        else {
            for(String phone:receivers){
                smsManager.sendTextMessage(phone,null,text,null,null);
            }
        }
    }

    //DB에서 전화번호 조회
    public List<String> getData() throws ExecutionException, InterruptedException {
        DBService repo = new DBService(mContext);
        List<String> data = repo.getAllPhone();

        return data;
    }

}