package com.example.rescueone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.amitshekhar.DebugDB;
import com.example.rescueone.R;
import com.example.rescueone.add_phones.AddPhonesActivity;
import com.example.rescueone.databinding.ActivityMainBinding;
import com.example.rescueone.db_phone.PreferenceManager;
import com.example.rescueone.db_server.User;
import com.example.rescueone.sos.FakeCallReceiver;
import com.example.rescueone.sos.GPSManager;
import com.example.rescueone.sos.MessageManager;
import com.example.rescueone.sos.NetworkManager;
import com.example.rescueone.sos.PushPayload;
import com.example.rescueone.sos.SirenPlayer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding mainBinding;
    String uid;
    User user;
    FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private SirenPlayer sp;
    private MessageManager sms;

    int typeState;

    GPSManager gm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        mainBinding.setActivity(this);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseMessaging.getInstance().subscribeToTopic("news");

        //내부db
        DebugDB.getAddressLog();
        typeState = PreferenceManager.getInt(this,"type");

        //초기화
        sms = new MessageManager(this);
        sp = new SirenPlayer(this);
    }
    @Override
    protected void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
        setUser(currentUser);
    }

    private void setUser(FirebaseUser currentUser){
        if (currentUser == null) {
            //로그인상태 유지가 안된 것이므로 다시 로그인화면으로 이동
            Intent intent = new Intent(getApplication(), LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        } else {//성공했을 때
            uid = currentUser.getUid();
            mDatabase.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {//비동기처리
                    user = dataSnapshot.getValue(User.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void updateUI(FirebaseUser currentUser) {
    }

    public void OnClick(View view){
            //연락처추가 버튼
            if (view.equals(mainBinding.addPhones)) {
                if(typeState==0){
                    Toast.makeText(this,"기기 사용자만 이용 가능한 메뉴입니다.",Toast.LENGTH_LONG).show();
                }else if(typeState==1) {
                    if (NetworkManager.checkNetworkStatus(this) == false) {
                        Toast.makeText(this, "wi-fi 혹은 모바일 데이터 연결이 필요합니다.", Toast.LENGTH_LONG).show();
                    } else {
                        Intent intent = new Intent(getApplication(), AddPhonesActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();
                    }
                }
            }
            //기기 연결 버튼
            else if (view.equals(mainBinding.device)) {
                if(typeState==0){
                    Toast.makeText(this,"기기 사용자만 이용 가능한 메뉴입니다.",Toast.LENGTH_LONG).show();
                }else if(typeState==1) {
                    if (NetworkManager.checkNetworkStatus(this) == false) {
                        Toast.makeText(this, "wi-fi 혹은 모바일 데이터 연결이 필요합니다.", Toast.LENGTH_LONG).show();
                    } else {
//                        Intent intent = new Intent(getApplication(), DeviceRegisterActivity.class);
//                        startActivity(intent);
//                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        Toast toast = Toast.makeText(this, "연결됨",Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            }
            //개인정보 설정 버튼
            else if (view.equals(mainBinding.settings)) {
                if(NetworkManager.checkNetworkStatus(this) == false){
                    Toast.makeText(this,"wi-fi 혹은 모바일 데이터 연결이 필요합니다.",Toast.LENGTH_LONG).show();
                }else {
                    if (currentUser != null) {
                        Intent intent = new Intent(getApplication(), SettingsActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();
                    } else {
                        mAuth.signOut();
                        Toast.makeText(MainActivity.this, "사용자 확인이 안 됩니다. 재로그인 혹은 사용자 등록을 해 주세요",
                                Toast.LENGTH_LONG).show();
                    }
                }
        }

            /*TEST버튼*/
            //SHORT KEY가 들어올 때
            if (view.equals(mainBinding.shortKey)) {
                setFakeCall();
            }
            //LONG KEY가 들어올 때
            else if (view.equals(mainBinding.longKey)) {
                //1.내가 등록한 사람들의 token 확보
                //2.그 토큰이 사용자 목록에 있는 지 없는 지 확인
                //3.해당 token으로 push알림 발송
                sms.sendSOS();
                getEmergencyContactToken();

                if(NetworkManager.checkNetworkStatus(this) == false) {
                    //긴급문자 발송
                    sms.sendSOS();
                }
                //사이렌 출력
                sp.playAudio();
            } else if (view.equals(mainBinding.stopSiren)) {
                if (sp != null)
                    sp.stopAudio();
            }

    }


    private void setFakeCall() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis()+1000);  //1초후 울림

        Intent intent = new Intent(getApplicationContext(), FakeCallReceiver.class);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        if(Build.VERSION.SDK_INT > 23) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
        else if(Build.VERSION.SDK_INT > 19) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }
    public static File getSaveFolder() {
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/SOSTok");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }


    //모든 uid의 token을 돌면서 내가 등록한 애랑 일치하는 애 찾는 함수
    private void getEmergencyContactToken() {
        ArrayList<String> tokens = new ArrayList<>();
        mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    //User 정보 한 명씩 가져와서 target에 저장(uid,이메일,emergencyContact전부다)
                    //target은 모든 사용자의 정보가 들어감
                    User target = ds.getValue(User.class);
                    //user정보 중 emergencyContact 속 정보를 하나씩 entry에 가져옴(전화번호,이름)
                    //entry는 특정 사용자의 연락처 속 사람들의 정보가 있음 target의 번호와 entry일치 확인
                    for(Map.Entry<String,String>entry : user.getEmergencyContact().entrySet()){
                        if(target.getPhoneNumber().equals(entry.getKey())){
                            tokens.add(target.getToken());
                        }else{//일치하는 사용자가 없으면 그냥 문자만 보내기
//                            sms.sendSOS();
                        }
                    }
                }
                for(String  t: tokens){
                    Log.e("token before push : ",t);
                }
                sendPush(tokens);
                for(String t:tokens){
                    Log.e("token after send : ",t);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendPush(ArrayList<String> tokens) {
        gm = new GPSManager(this);
        String location = gm.getAddress();
        Log.e("location:",location);
        for(String t:tokens){
            //index.js에 따라
            String key = mDatabase.child("push").child("token").push().getKey();
            PushPayload data = new PushPayload();//push정보 저장
            data.setKey(key);
            data.setTitle(user.getName()+"님의 응급신호");
            data.setMessage( "[Rescue ONE]\n"+user.getName()+
                    "님이 응급신호를 보냈습니다.\n위치:"+location);
            data.setToken(t);
            mDatabase.child("push").child("token").child(key).setValue(data);
        }
    }


}
