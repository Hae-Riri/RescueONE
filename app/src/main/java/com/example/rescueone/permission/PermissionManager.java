package com.example.rescueone.permission;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;

public class PermissionManager {

    private static final int PERMISSION_REQUEST = 100;
    private static String[] myPermission = {
            Manifest.permission.ACCESS_COARSE_LOCATION,     //위치 권한
            Manifest.permission.ACCESS_FINE_LOCATION,       //위치 권한
            Manifest.permission.RECORD_AUDIO,                //녹음 권한
            Manifest.permission.SEND_SMS,                    //문자 발송 권한
            Manifest.permission.WRITE_EXTERNAL_STORAGE,     //파일쓰기 권한
            Manifest.permission.READ_EXTERNAL_STORAGE       //파일읽기 권한

    };

    //모든 권한에 대하여 확인
    public static boolean checkAllPermissions(Activity activity){
        int result;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//마시멜로우 이상
            for (String p : myPermission) {//허용이 필요한 권한 확인
                result = ActivityCompat.checkSelfPermission(activity, p);
                if (result != PackageManager.PERMISSION_GRANTED) {//권한이 허용되어있지 않을 경우
                    return false;
                }
            }
        }
        return true;
    }

    //1개 권한에 대하여 확인
    public static boolean checkPermission(Activity activity, String permission){
        int result = ActivityCompat.checkSelfPermission(activity, permission);
        if(result == PackageManager.PERMISSION_GRANTED)
            return true;
        else
            return false;
    }

    //모든 권한 요청
    public static void getAllPermissions(Activity activity){
        ActivityCompat.requestPermissions(activity, myPermission, PERMISSION_REQUEST);
    }

    //1개 권한 요청
    public static void getPermission(Activity activity, String permission){
        ActivityCompat.requestPermissions(activity,new String[]{permission},PERMISSION_REQUEST);
    }
}
