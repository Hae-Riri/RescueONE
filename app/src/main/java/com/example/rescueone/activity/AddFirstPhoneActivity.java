package com.example.rescueone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.rescueone.R;
import com.example.rescueone.databinding.ActivityAddFirstPhoneBinding;
import com.example.rescueone.db_phone.DBService;
import com.example.rescueone.db_phone.PreferenceManager;
import com.example.rescueone.db_phone.ReceiveData;
import com.example.rescueone.db_server.EmergencyContact;
import com.example.rescueone.db_server.User;
import com.example.rescueone.permission.PermissionManager;
import com.example.rescueone.sos.MessageManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AddFirstPhoneActivity extends AppCompatActivity {

    EditText name;
    EditText phone;
    ImageButton addFirst;
    Toolbar toolbar;

    ActivityAddFirstPhoneBinding mBinding;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    FirebaseUser currentUser = mAuth.getCurrentUser();

    String uid = currentUser.getUid();
    User user;
    ArrayList<EmergencyContact> datas = new ArrayList<>();

    //내부db
    private static final int PERMISSION_REQUEST = 100;
    private DBService dbService;
    private MessageManager sms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_add_first_phone);

        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        addFirst = findViewById(R.id.addFirst);
        toolbar = findViewById(R.id.toolBar);

        phone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        //권한 설정 허용 확인
        if(PermissionManager.checkPermission(this, Manifest.permission.SEND_SMS)){
            PermissionManager.getPermission(this, Manifest.permission.SEND_SMS);
        }

        //초기화
        sms = new MessageManager(this);
        dbService = new DBService(this);

        //최초 연락처 등록버튼 클릭 시 이벤트
        addFirst.setOnClickListener(view -> {
            //서버
            new EmergencyContact(name.getText().toString(),
                    phone.getText().toString());
            Log.e("first add",uid+" "+phone.getText().toString());
            mDatabase.child("users").child(uid).child("emergencyContact").child(phone.getText().toString()).setValue(name.getText().toString());

            //내부
            if(!PermissionManager.checkPermission(this, Manifest.permission.SEND_SMS)){
                finishAffinity();
            }
            PreferenceManager.set(AddFirstPhoneActivity.this,"registration",1);
            String receiverName = name.getText().toString();
            String receiverPhone = phone.getText().toString();

            if(!receiverName.equals("") && !receiverPhone.equals("")) {
                //sms전송
                sms.sendLink(receiverPhone);
                //DB 데이터 삽입
                dbService.insert(new ReceiveData(receiverName, receiverPhone));

                Toast.makeText(this,"비밀번호 설정 메일이 전송되었습니다. 로그인 후 사용하세요.",Toast.LENGTH_SHORT).show();
                //로그인화면으로 이동
                Intent intent = new Intent(getApplication(), LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
            else{
                Toast.makeText(this,"모든 정보를 입력해주세요.",Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void OnClick(View view){
        if(view.equals(R.id.root)){
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mBinding.name.getWindowToken(),0);
            imm.hideSoftInputFromWindow(mBinding.phone.getWindowToken(),0);
        }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST:{
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++) {
                        if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                            //권한 거부 시 설정으로 이동
                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setTitle("권한 설정 요청")
                                    .setMessage("권한을 허용해야 이용가능합니다.")
                                    .setPositiveButton("설정으로 이동", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            intent.setData(Uri.parse("package:" + getPackageName()));
                                            startActivity(intent);
                                        }
                                    })
                                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Toast.makeText(AddFirstPhoneActivity.this, "권한 허용이 필요합니다.", Toast.LENGTH_SHORT).show();
                                            finishAffinity();
                                        }
                                    }).create().show();
                            break;
                        }
                        else{
                            break;
                        }
                    }
                }
            }
        }
    }

}
