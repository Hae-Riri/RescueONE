package com.example.rescueone.db_phone;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {ReceiveData.class},version = 1,exportSchema = false)
public abstract class ReceiveDatabase extends RoomDatabase {
    public abstract ReceiveDataDao receiveDataDao();

    private static ReceiveDatabase Instance;

    private static final Object sLock = new Object();

    public static ReceiveDatabase getInstance(Context context) {//싱글톤
        synchronized (sLock) {
            if(Instance==null) {
                Instance= Room.databaseBuilder(context.getApplicationContext(),
                        ReceiveDatabase.class, "Receivers-db")
                        .build();
            }
            return Instance;
        }
    }

    public static void destroyInstance(){
        Instance = null;
    }
}
