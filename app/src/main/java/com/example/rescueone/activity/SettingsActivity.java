package com.example.rescueone.activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.rescueone.R;
import com.example.rescueone.databinding.ActivitySettingsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingsActivity extends AppCompatActivity {

    ActivitySettingsBinding mBinding;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
        mBinding.setActivity(this);
        Button logout = findViewById(R.id.logout);
        Button deleteEmail = findViewById(R.id.deleteEmail);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Toast.makeText(SettingsActivity.this, "로그아웃됩니다, 다시 로그인하세요.",
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplication(), LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        deleteEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.getCurrentUser().delete();
                //TODO: db에서 지우는 코드
                Toast.makeText(SettingsActivity.this, "회원탈퇴합니다.",
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplication(), LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

    }
//    public void OnClick(View view){
//        if(view.equals(R.id.logout)){
//            mAuth.signOut();
//            Intent intent = new Intent(getApplication(),LoginActivity.class);
//            startActivity(intent);
//            overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
//            finish();
//        }else if(view.equals(R.id.deleteEmail)){
//            mAuth.getCurrentUser().delete();
//        }
//    }
}
