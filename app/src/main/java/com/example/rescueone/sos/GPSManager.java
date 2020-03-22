package com.example.rescueone.sos;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class GPSManager {
    private static final long MIN_TIME = 1000;     //1초
    private static final float MIN_DISTANCE = 0;    //0m

    private Context mContext;
    private LocationManager mLocationManager;
    private GPSListener mGPSListener;
    private Geocoder mGeocoder;

    private double longitude;
    private double latitude;

    public GPSManager(Context context) {
        mContext = context;
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        mGPSListener = new GPSListener();
        mGeocoder = new Geocoder(mContext, Locale.getDefault());
    }

    public void getLocation() {
        if(Build.VERSION.SDK_INT >= 23) {
            if (mContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && mContext.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            }
        }

        /*위치정보 획득*/
        Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);    //GPS 기준 조회
        if(location == null){   //GPS 조회 실패일 경우, network로 조회
            location= mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        if(location !=null){
            longitude = location.getLongitude();
            latitude = location.getLatitude();
        }

        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME,MIN_DISTANCE,mGPSListener);
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,MIN_TIME,MIN_DISTANCE,mGPSListener);
    }

    public String getAddress(){
        String address = "위치정보 미수신";
        ArrayList<Address> results;      //주소 결과값 배열

        getLocation();                   //위치수신

        try {
            results = (ArrayList<Address>) mGeocoder.getFromLocation(latitude,longitude,1);  //주소 1개 반환
            if (results != null && results.size()>0){
                address = results.get(0).getAddressLine(0).replace("대한민국","");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }
}


//네트워크 없이 받기
//public class GPSManager {
//    private Context mContext;
//    private LocationManager mLocationManager;
//    private Criteria mCriteria;
//    private Geocoder mGeocoder;
//
//    private double longitude;
//    private double latitude;
//
//    public GPSManager(Context context) {
//        mContext = context;
//        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
//        mCriteria = new Criteria();
//        mGeocoder = new Geocoder(mContext, Locale.getDefault());
//    }
//
//    public void getLocation() {
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (mContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && mContext.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//
//            }
//        }
//
//        /*위치정보 획득*/
//        String provider = mLocationManager.getBestProvider(mCriteria,true);     //가장 적합한 GPS Provider
//        Location location = mLocationManager.getLastKnownLocation(provider);
//
//        if(location !=null){
//            longitude = location.getLongitude();
//            latitude = location.getLatitude();
//        }
//    }
//
//    public String getAddress(){
//        String address = "위치정보 미수신";
//        ArrayList<Address> results;      //주소 결과값 배열
//
//        try {
//            results = (ArrayList<Address>) mGeocoder.getFromLocation(latitude,longitude,1);  //주소 1개 반환
//            if (results != null && results.size()>0){
//                address = results.get(0).getAddressLine(0).replace("대한민국","");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return address;
//    }
//}