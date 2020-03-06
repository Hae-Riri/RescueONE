package com.example.rescueone.db_phone;

import android.content.Context;
import android.os.AsyncTask;

import androidx.room.Room;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class DBService {

    private static ReceiveDatabase db;
    private static ReceiveDataDao mDao;

    public DBService(Context context){
        db = Room.databaseBuilder(context.getApplicationContext(),ReceiveDatabase.class,"Receivers-db").build();
        mDao = db.receiveDataDao();
    }

    public void insert(ReceiveData receiveData){
        new InsertAsyncTask().execute(receiveData);

    }

    //DB 데이터 삽입
    private static class InsertAsyncTask extends AsyncTask<ReceiveData,Void,Void> {

        @Override
        protected Void doInBackground(ReceiveData... receiveData) {
            mDao.insert(receiveData[0]);
            return null;
        }
    }

    //DB 데이터 삭제(Phone 기준)
    public void deleteByPhone(String num){
        new deleteByphoneAsyncTask().execute(num);
    }

    //비동기처리(삭제)
    private static class deleteByphoneAsyncTask extends AsyncTask<String,Void,Void> {
        @Override
        protected Void doInBackground(String... receiveData) {
            mDao.deleteByPhone(receiveData[0]);
            return null;
        }
    }

    //전화번호 데이터 조회
    public List<String> getAllPhone() throws ExecutionException, InterruptedException {
        return new getPhoneAsyncTask().execute().get();
    }

    //비동기처리(데이터 조회)
    private static class getPhoneAsyncTask extends AsyncTask<Void,Void,List<String>> {

        @Override
        protected List<String> doInBackground(Void...url) {
            return mDao.getAllPhone();
        }
    }
}
