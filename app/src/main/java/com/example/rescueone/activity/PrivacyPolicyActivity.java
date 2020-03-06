package com.example.rescueone.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.example.rescueone.R;
import com.example.rescueone.db_phone.PreferenceManager;

public class PrivacyPolicyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        //취소버튼
        Button cancelBtn = (Button)findViewById(R.id.cancel);
        cancelBtn.setOnClickListener(v->{
            Toast toast = Toast.makeText(this,R.string.denyToast,Toast.LENGTH_SHORT);
            toast.show();
            finishAffinity();   //어플 종료
        });
        //동의버튼
        Button agreeBtn = (Button)findViewById(R.id.agree);
        agreeBtn.setOnClickListener(v->{
            //동의여부 저장
            PreferenceManager.set(this,"privacyPolicy",true);
            //사용자 등록화면으로 전환
            Intent intent = new Intent(getApplication(), UserRegisterActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        });

    }
}
