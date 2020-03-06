package com.example.rescueone.db_phone;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface ReceiveDataDao {
    //DB데이터 전체 반환
    @Query("SELECT * FROM ReceiveData")
    LiveData<List<ReceiveData>> getLiveData();

    //DB데이터 전체 삭제
    @Query("DELETE FROM ReceiveData")
    void clearAll();

    //데이터 삽입
    @Insert(onConflict = REPLACE)
    void insert(ReceiveData receiveData);

    //데이터 갱신
    @Update
    void update(ReceiveData receiveData);

    //데이터 삭제
    @Delete
    void delete(ReceiveData receiveData);

    //데이터 삭제(번호 기준)
    @Query("DELETE FROM receivedata WHERE phone = :num")
    int deleteByPhone(String num);

    //폰 번호 받기
    @Query("SELECT phone FROM ReceiveData")
    List<String> getAllPhone();
}
