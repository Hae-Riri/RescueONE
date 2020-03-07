package com.example.rescueone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.rescueone.R;
import com.example.rescueone.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding mBinding;
    private static final String TAG = "LoginActivity";
    private FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding= DataBindingUtil.setContentView(this,R.layout.activity_login);
        mBinding.setActivity(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }
    public void OnClick(View view){
        if(view.equals(mBinding.findPassword)){
            Intent intent = new Intent(this, FindPasswordActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        }else if(view.equals(mBinding.login)){
            login();
        }else if(view.equals(mBinding.registerUser)){
            Intent intent = new Intent(this, PrivacyPolicyActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        }else if(view.equals(mBinding.root)){//바깥화면 터치 시 키보드 없애기
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mBinding.id.getWindowToken(),0);
            imm.hideSoftInputFromWindow(mBinding.password.getWindowToken(),0);
        }
    }
    private void login() {
        mAuth = FirebaseAuth.getInstance();

        String email = mBinding.id.getText().toString();
        String password = mBinding.password.getText().toString();

        //기존 사용자 로그인
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "잘못된 정보입니다.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });

    }

    private void updateUI(FirebaseUser user) {
        if(user!=null){//로그인 성공 시 UID 확보, Token get하기
            uid = user.getUid();//FirebaseUser의 메소드
            getToken();
        }
    }

    private void getToken() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if(! task.isSuccessful()){//토큰가져오기 실패하면
                            Log.w(TAG,"getInstance failed",task.getException());
                            return;
                        }

                        //Get new Instance ID token
                        String token = task.getResult().getToken();
                        Log.e(TAG,token);
                        saveToken(token);
                    }
                });
    }

    //token을 서버 db에 저장하기
    private void saveToken(String token) {
        //users 탭 밑에 uid 자체로 항목을 나누고 그 내에 token이라는 탭의 토큰 값 넣기
        mDatabase.child("users").child(uid).child("token").setValue(token)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //로그인 성공이면
                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                        finish();

                    }
                });
    }
}
