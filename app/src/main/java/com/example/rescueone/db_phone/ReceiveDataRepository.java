package com.example.rescueone.db_phone;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class ReceiveDataRepository {
    private static ReceiveDataDao mDao;
    private LiveData<List<ReceiveData>> allData;

    public ReceiveDataRepository(Application application) {
        ReceiveDatabase db = ReceiveDatabase.getInstance(application);
        this.mDao = db.receiveDataDao();
        this.allData = mDao.getLiveData();
    }

    public LiveData<List<ReceiveData>> getLiveData(){
        return allData;
    }

    //DB 데이터 삽입
    public void insertData(ReceiveData rc){
        new insertAsyncTask().execute(rc);
    }

    //비동기처리(삽입)
    private static class insertAsyncTask extends AsyncTask<ReceiveData,Void,Void> {
        @Override
        protected Void doInBackground(ReceiveData... receiveData) {
            mDao.insert(receiveData[0]);
            return null;
        }
    }

    //DB 데이터 삭제
    public void deleteData(ReceiveData rc){
        new deleteAsyncTask().execute(rc);
    }

    //비동기처리(삭제)
    private static class deleteAsyncTask extends AsyncTask<ReceiveData,Void,Void> {
        @Override
        protected Void doInBackground(ReceiveData... receiveData) {
            mDao.delete(receiveData[0]);
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
    public  List<String> getAllPhone() throws ExecutionException, InterruptedException {
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
