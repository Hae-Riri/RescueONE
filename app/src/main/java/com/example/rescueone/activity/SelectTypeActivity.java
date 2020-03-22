package com.example.rescueone.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.example.rescueone.R;
import com.example.rescueone.db_phone.PreferenceManager;
import com.example.rescueone.permission.PermissionActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SelectTypeActivity extends AppCompatActivity {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_type);

        //'네''버튼 동작
        Button yesBtn = (Button)findViewById(R.id.yes);
        yesBtn.setOnClickListener(v->{
            PreferenceManager.set(this,"type",1);   //사용자 상태 저장
            Intent intent = new Intent(SelectTypeActivity.this, PermissionActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
            Toast.makeText(SelectTypeActivity.this, "로그인 후 반드시 기기를 등록해주세요.",
                    Toast.LENGTH_LONG).show();
        });
        //'아니오''버튼 동작
        Button noBtn = (Button)findViewById(R.id.no);
        noBtn.setOnClickListener(v->{
            PreferenceManager.set(this,"type",0);
            PreferenceManager.set(this,"permission",0);     //권한여부 저장
            PreferenceManager.set(this,"registration",0);   //첫 등록 화면 저장
            if(currentUser ==null) {
                Intent intent = new Intent(SelectTypeActivity.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }else{
                Intent intent = new Intent(SelectTypeActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
    }
}
