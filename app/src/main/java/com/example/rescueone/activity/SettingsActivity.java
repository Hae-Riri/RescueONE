package com.example.rescueone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.amitshekhar.DebugDB;
import com.example.rescueone.R;
import com.example.rescueone.databinding.ActivitySettingsBinding;
import com.example.rescueone.db_phone.PreferenceManager;
import com.example.rescueone.db_server.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingsActivity extends AppCompatActivity{

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    String uid;
    DatabaseReference mDatabase;
    EditText editNumber;
    Button btnEditNumber;
    TextView textName;
    TextView textEmail;
    ImageView back;

    String phone;
    String name;
    String email;
    User user;


    ActivitySettingsBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_settings);

//        mAuth = FirebaseAuth.getInstance();
//        currentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        uid = currentUser.getUid();

        editNumber = findViewById(R.id.editNumber);
        editNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());  //자동 하이픈(-) 생성
        btnEditNumber = findViewById(R.id.btnEditNumber);
        textEmail = findViewById(R.id.textEmail);
        textName = findViewById(R.id.textName);
        back = findViewById(R.id.back);

        name = PreferenceManager.getString(this, "name");
        phone = PreferenceManager.getString(this, "phone");
        email = PreferenceManager.getString(this, "email");
        textEmail.setText(email);
        editNumber.setText(phone);
        textName.setText(name);


        Button logout = findViewById(R.id.logout);

        btnEditNumber.setOnClickListener(new View.OnClickListener() {
            String newPhone = editNumber.getText().toString();

            @Override
            public void onClick(View view) {
                mDatabase.child("users").child(uid).child("phoneNumber").setValue(editNumber.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(SettingsActivity.this, "입력하신 번호로 사용자정보가 변경되었습니다.", Toast.LENGTH_LONG).show();
                            }
                        });
                PreferenceManager.set(SettingsActivity.this, "phone", newPhone);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(getApplication(), MainActivity.class);
                startActivity(intent2);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });


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


    }
    public void OnClick(View view){
        if(view.equals(mBinding.btnChangeType)){
            Intent intent = new Intent(getApplication(), SelectTypeActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }else if(view.equals(mBinding.deleteEmail)){
            mAuth.getCurrentUser().delete();
            mDatabase.child("users").child(uid).removeValue();
            Intent intent = new Intent(getApplication(), LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            Toast.makeText(SettingsActivity.this, "회원탈퇴합니다.",
                    Toast.LENGTH_SHORT).show();
        }
    }
}

//        Button logout = findViewById(R.id.logout);
//        Button deleteEmail = findViewById(R.id.deleteEmail);
//
//        logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mAuth.signOut();
//                Toast.makeText(SettingsActivity.this, "로그아웃됩니다, 다시 로그인하세요.",
//                        Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(getApplication(), LoginActivity.class);
//                startActivity(intent);
//                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//                finish();
//            }
//        });
//
//        deleteEmail.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mAuth.getCurrentUser().delete();
//                //TODO: db에서 지우는 코드
//                Toast.makeText(SettingsActivity.this, "회원탈퇴합니다.",
//                        Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(getApplication(), LoginActivity.class);
//                startActivity(intent);
//                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//                finish();
//            }
//        });
//
//    }
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

