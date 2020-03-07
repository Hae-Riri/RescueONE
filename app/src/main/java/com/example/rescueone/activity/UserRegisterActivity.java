package com.example.rescueone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.rescueone.R;
import com.example.rescueone.databinding.ActivityUserRegisterBinding;
import com.example.rescueone.db_phone.PreferenceManager;
import com.example.rescueone.db_server.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

public class UserRegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterUserActivity";
    ActivityUserRegisterBinding mBinding;
    private FirebaseAuth mAuth;
    ProgressDialog dialog; //진행중 표시 다이얼로그
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding= DataBindingUtil.setContentView(this, R.layout.activity_user_register);
        mBinding.setActivity(this);

        mAuth = FirebaseAuth.getInstance();

        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mBinding.phone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());  //자동 하이픈(-) 생성
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
        if(view.equals(mBinding.btnRegister)){//사용자 등록 버튼 클릭 시
            join();
            joinPhoneDB();
        }else if(view.equals(mBinding.root)){//바깥화면 터치 시 키보드 없애기
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mBinding.email.getWindowToken(),0);
            imm.hideSoftInputFromWindow(mBinding.name.getWindowToken(),0);
            imm.hideSoftInputFromWindow(mBinding.phone.getWindowToken(),0);
        }
    }

    //폰내부db
    private void joinPhoneDB() {

        String userName = mBinding.name.getText().toString();
        String userPhone = mBinding.phone.getText().toString();
        if(!userName.equals("") && !userPhone.equals("")){
            //이름과 전화번호 저장
            PreferenceManager.set(this,"name",userName);
            PreferenceManager.set(this,"phone",userPhone);
            //최초 등록화면으로 전환
            Intent intent = new Intent(UserRegisterActivity.this, SelectTypeActivity.class);
            startActivity(intent);
        }
        else {//공백일 경우
            Toast.makeText(this,"모든 정보를 입력해주세요.",Toast.LENGTH_SHORT).show();
        }
    }

    //사용자 첫 등록 함수
    private void join() {
        dialog = ProgressDialog.show(this,"","회원정보 등록 중입니다."+"\n"+"비밀번호 재설정 이메일이 전송됩니다.",true);//intermediate는 다이얼로그가 꺼지지 않게 하는 것

        String email = mBinding.email.getText().toString();
        String password = UUID.randomUUID().toString().substring(0,8);
        //비밀번호는 랜덤으로 일단 배부 ,  나중에 비번 변경해야 함

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(UserRegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });

    }

    private void updateUI(FirebaseUser user) {
        if(user!=null){//user 정보가 제대로 들어오면 data에 업로드하기
            updateUserData(user);
        }
    }

    private void updateUserData(FirebaseUser firebaseUser) {
        User user = new User();//User 객체 생성
        user.setUid(firebaseUser.getUid());
        user.setEmail(mBinding.email.getText().toString());
        user.setName(mBinding.name.getText().toString());
        user.setPhoneNumber(mBinding.phone.getText().toString());

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users")
                .child(firebaseUser.getUid());

        myRef.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                sendPasswordResetEmail();
            }
        });
    }

    private void sendPasswordResetEmail() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String emailAddress = mBinding.email.getText().toString();

        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        dialog.dismiss();//다이얼로그 끄기
                        Intent intent = new Intent(getApplication(),SelectTypeActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                        finish();
                    }
                });
    }

}
