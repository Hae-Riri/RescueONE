package com.example.rescueone.activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.rescueone.R;
import com.example.rescueone.databinding.ActivityFindPasswordBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FindPasswordActivity extends AppCompatActivity{
    private static final String TAG = "FindPasswordActivity";
    ActivityFindPasswordBinding mBinding;
    ProgressDialog dialog;
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_find_password);
        mBinding.setActivity(this);

    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void OnClick(View view){
        if(view.equals(mBinding.send)){
            if(!mBinding.email.getText().toString().matches(getString(R.string.email_match_checker))){
                Toast.makeText(this,"이메일 형식으로 입력해주세요.",Toast.LENGTH_SHORT).show();
            }else{
                findPassword();
            }
        }
    }

    private void findPassword() {
        dialog = ProgressDialog.show(this, "", "잠시만 기다려주세요.", true);//interminate true면 안꺼짐
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String emailAddress = mBinding.email.getText().toString();

        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {//이메일추적방지, 틀릴때도맞을때모두발송
                        Toast.makeText(FindPasswordActivity.this, "비밀번호 재설정 이메일을 보냈습니다.", Toast.LENGTH_SHORT).show();
                        finish();
                        dialog.dismiss();

                        currentUser = mAuth.getCurrentUser();

                        if(currentUser !=null){
                            Intent intent = new Intent(getApplication(), LoginActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            finish();
                        }
                    }
                });
    }
}