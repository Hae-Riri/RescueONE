package com.example.rescueone.sos;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

public class NetworkManager {
    private static Context mContext;
    private static ConnectivityManager mConManager;

    private static void init(Context context){
        mContext = context;
        mConManager = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public static boolean checkNetworkStatus(Context context){
        init(context);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            NetworkCapabilities mNetCap =  mConManager.getNetworkCapabilities(mConManager.getActiveNetwork());
            if (mNetCap != null){
                return true;
            }
        }
        else{
            NetworkInfo mNetInfo = mConManager.getActiveNetworkInfo();
            if (mNetInfo != null){
                return true;
            }
        }
        return false;
    }
}
