package com.example.rescueone.sos;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;

import com.example.rescueone.activity.MainActivity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RecordManager {

    private Context mContext;
    private MediaRecorder mMediaRecorder;
    private String path;

    public RecordManager(Context context) {
        mContext = context;
        path = MainActivity.getSaveFolder().getAbsolutePath();  //파일저장 경로
        initRecorder();
    }

    public void initRecorder() {
        stopRecord();

        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);        //오디오 입력 설정
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);   //출력파일 확장자
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);  //인코딩 코덱
        mMediaRecorder.setAudioSamplingRate(44100);                          //샘플링 비율(고음질)
        mMediaRecorder.setAudioEncodingBitRate(96000);                       //비트레이트(고음질)
    }

    public void startRecord() {
        String FILENAME = "/Recorded_" + getToday() + ".mp3";   //파일명

        mMediaRecorder.setOutputFile(path+FILENAME);
        try {
            mMediaRecorder.prepare();
            mMediaRecorder.start();
        }
        catch(IOException | RuntimeException e) {
            e.printStackTrace();
        }
    }

    public void stopRecord() {
        if (mMediaRecorder != null) {
            mMediaRecorder.stop();
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
        else
            return;
    }

    public static String getToday(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd_hh:mm:ss");
        return sdf.format(new Date());
    }

    private void saveRecord(){
        String FILENAME = "Recorded_" + getToday() + ".mp3";   //파일명
        ParcelFileDescriptor file = null;

        ContentValues content = new ContentValues();
        content.put(MediaStore.Audio.Media.DISPLAY_NAME, FILENAME);
        content.put(MediaStore.Audio.Media.MIME_TYPE, "audio/mp3");
        content.put(MediaStore.Audio.Media.RELATIVE_PATH, path);

        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.Q){
            content.put(MediaStore.Files.FileColumns.RELATIVE_PATH, Environment.DIRECTORY_MUSIC);
        }

        ContentResolver contentResolver = mContext.getContentResolver();
        Uri audio = contentResolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, content);

        try {
            file = contentResolver.openFileDescriptor(audio, "w");
            if (file == null){
                Log.d("filewrire","fail");
            }
            else{

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
