package com.example.rescueone.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.rescueone.R;
import com.example.rescueone.db_phone.PreferenceManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser==null){
            Toast.makeText(this,"자동 로그인 중입니다.",Toast.LENGTH_SHORT);
            Intent intent = new Intent(getApplication(),LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
            finish();
        }else if(currentUser !=null){
            updateUI(currentUser);
            Intent intent = new Intent(getApplication(),MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
            finish();
        }
    }

    private void updateUI(FirebaseUser currentUser) {
        if(currentUser!=null){//로그인 성공 시 UID 확보, Token get하기
            uid = currentUser.getUid();
        }
    }

}
