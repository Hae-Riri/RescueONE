package com.example.rescueone.permission;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.rescueone.R;
import com.example.rescueone.activity.AddFirstPhoneActivity;
import com.example.rescueone.db_phone.PreferenceManager;

public class PermissionActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);
        //확인 버튼 연결
        Button ok = (Button)findViewById(R.id.permissionCheckBtn);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                if(!PermissionManager.checkAllPermissions(PermissionActivity.this)){
                    PermissionManager.getAllPermissions(PermissionActivity.this);
                    PreferenceManager.set(PermissionActivity.this,"permission",1);
                }
                else {
                    Intent intent = new Intent(PermissionActivity.this, AddFirstPhoneActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in,R.anim.fade_out);                }
            }
        });

        //안드로이드 6.0일 경우, 권한 설정 확인, 버전 6 미만은 설치시 권한 허용
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
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
                                            Toast.makeText(PermissionActivity.this, "권한 허용이 필요합니다.", Toast.LENGTH_SHORT).show();
                                            finishAffinity();
                                        }
                                    }).create().show();
                            break;
                        } else {
                            Intent intent = new Intent(PermissionActivity.this, AddFirstPhoneActivity.class);
                            startActivity(intent);
                            break;
                        }
                    }
                }
            }
        }
    }
}
