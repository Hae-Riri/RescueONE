package com.example.rescueone.sos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.KeyguardManager;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.example.rescueone.R;

public class FakeCallActivity extends AppCompatActivity {

    private ConstraintLayout defaultDisplay;
    private ConstraintLayout recordingDisplay;
    private Ringtone ringtone;
    private RecordManager recordManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fake_call);

        /*잠금화면 위로 activity 올림*/
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
            KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
            keyguardManager.requestDismissKeyguard(this, null);
        }
        else{
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED); //잠금화면 위로 출력
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //화면 on 유지
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);   //꺼진화면 on
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD); //키가드 해제
        }

        //녹음
        recordManager = new RecordManager(this);

        //화면 초기화
        defaultDisplay = (ConstraintLayout)findViewById(R.id.fakecall_intro);
        defaultDisplay.setVisibility(View.VISIBLE);
        recordingDisplay = (ConstraintLayout)findViewById(R.id.fakecall_recording);
        recordingDisplay.setVisibility(View.GONE);

        //벨소리 출력
        ring();

        //녹음버튼 동작
        ImageButton callBtn = (ImageButton)findViewById(R.id.fakecall_call);
        callBtn.setOnClickListener(v->{
            defaultDisplay.setVisibility(View.GONE);            //화면전환
            recordingDisplay.setVisibility(View.VISIBLE);
            ringtone.stop();                                    //벨소리 종료
            recordManager.startRecord();                         //녹음 시작
        });

        //끊기버튼 동작
        ImageButton denyBtn = (ImageButton)findViewById(R.id.fakecall_deny);
        denyBtn.setOnClickListener(v->{
            //벨소리 종료
            ringtone.stop();
            finish();
        });

        //녹음중단 버튼 동작
        ImageButton stopBtn = (ImageButton)findViewById(R.id.fakecall_stop);
        stopBtn.setOnClickListener(v->{
            recordManager.stopRecord();        //녹음종료
            finish();
        });
    }
    public void ring() {
        /*벨소리 울림*/
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        ringtone = RingtoneManager.getRingtone(getApplicationContext(), uri);

        if (Build.VERSION.SDK_INT >= 21) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build();
            ringtone.setAudioAttributes(audioAttributes);
            ringtone.play();
        }
    }
}
