package com.example.rescueone.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.rescueone.R;
import com.example.rescueone.ble.BLEUtil;
import com.example.rescueone.ble.ConnectResult;
import com.example.rescueone.ble.LeDeviceListAdapter;
import com.example.rescueone.ble.ScanService;
import com.example.rescueone.databinding.ActivityDeviceRegisterBinding;
import com.example.rescueone.db_phone.DBService;
import com.example.rescueone.db_phone.PreferenceManager;
import com.example.rescueone.sos.FakeCallReceiver;
import com.example.rescueone.sos.GPSManager;
import com.example.rescueone.sos.MessageManager;
import com.example.rescueone.sos.PushPayload;
import com.example.rescueone.sos.SirenPlayer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.example.rescueone.ble.ConnectService.mConnectResult;

public class DeviceRegisterActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    ActivityDeviceRegisterBinding mBinding;
    SirenPlayer sp1;

    private static final String TAG = "SERVICE.SCAN";

    private ScanService scanService;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LeDeviceListAdapter mLeDeviceListAdapter;

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_register);

        mContext = this;
        sp1 = new SirenPlayer(this);


        //새로고침
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(this);

        mLeDeviceListAdapter = new LeDeviceListAdapter();
        ListView deviceList = (ListView)findViewById(R.id.deviceList);
        deviceList.setAdapter(mLeDeviceListAdapter);
        deviceList.setOnItemClickListener(onItemClickListener);

        scanService = new ScanService(getApplicationContext(),this, mLeDeviceListAdapter);
        scanService.scanDevice(true);

    }//onCreate끝

    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            final BluetoothDevice device = mLeDeviceListAdapter.getDevice(i);
            if (device == null) {
                Log.d(TAG,"Device not found");
                return;
            }
            if(scanService.mScanning){
                scanService.stopScan();
            }
            BLEUtil.startConnectService(getApplicationContext(),mConnectResult,device);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        //BLE 지원 여부 확인
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
            Toast.makeText(this,"BLE지원안함", Toast.LENGTH_SHORT).show();
            finishAffinity();       //어플 종료
        }
    }

    private ConnectResult mConnectResult = new ConnectResult() {
        @Override
        public void onConnectResult(boolean result, BluetoothDevice device) {
            if(result){
                Toast.makeText(getApplicationContext(),"연결됨",Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getApplicationContext(),"실패",Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onEmergencyShort(String key) {
            Log.d(TAG, key+" return");
            ((MainActivity)MainActivity.mContext).setFakeCall();
//            if(key.indexOf("S")==0){
//                Log.d(TAG,"long ok");
//                return;
////                Toast.makeText(DeviceRegisterActivity.this,"long",Toast.LENGTH_SHORT).show();
////                ((MainActivity)MainActivity.mContext).sendSOS();
////                ((MainActivity)MainActivity.mContext).getEmergencyContactToken();
////                playsp();
////                Intent intent = new Intent(getApplication(),MainActivity.class);
////                startActivity(intent);
//            }


        }

        @Override
        public void onEmergencyLong(String key) {
            Log.d(TAG, key+" return");
            Log.d(TAG, "안녕 나는 롱이야");
            ((MainActivity)MainActivity.mContext).getEmergencyContactToken();
            ((MainActivity)MainActivity.mContext).playAudio();
            ((MainActivity)MainActivity.mContext).sendSOS();
            Intent intent = new Intent(getApplication(),MainActivity.class);
            startActivity(intent);
            //sendSOS();
        }
    };

    private void sendSOS() {
        String userName;
        SmsManager smsManager;

        userName = PreferenceManager.getString(mContext, "name");
        smsManager = SmsManager.getDefault();
        List<String> receivers = null;
        try {
            receivers = getData();
        } catch (ExecutionException e) {
            Log.d("메세지","실패");
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d("메세지","실패");
        }

        GPSManager gm = new GPSManager(mContext);
        //위치정보 획득
        //gm.getLocation();
        String location = gm.getAddress();
        String text = "[Rescue ONE]\n" + userName +"님이 긴급한 상황에 처했습니다.\n위치: "+location;
        if(receivers == null){

        }
        else {
            for(String phone:receivers){
                smsManager.sendTextMessage(phone,null,text,null,null);
            }
        }

    }


    //DB에서 전화번호 조회
    public List<String> getData() throws ExecutionException, InterruptedException {
        DBService repo = new DBService((MainActivity)MainActivity.mContext);
        List<String> data = repo.getAllPhone();

        return data;
    }




    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mLeDeviceListAdapter.clear();
                if(scanService.mScanning){
                    scanService.stopScan();
                }
                scanService.scanDevice(true);
                swipeRefreshLayout.setRefreshing(false);
            }
        },1000);
    }

    @Override
    protected void onDestroy() {
        if(scanService.mScanning){
            scanService.stopScan();
        }
        super.onDestroy();
    }




}
