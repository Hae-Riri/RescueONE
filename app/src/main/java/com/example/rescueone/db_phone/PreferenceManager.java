package com.example.rescueone.db_phone;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {
    public static final String PREFERENCES_NAME = "UserInfo";
    private static final String DEFAULT_VALUE_STRING = "";
    private static final boolean DEFAULT_VALUE_BOOLEAN = false;
    private static final int DEFAULT_VALUE_INT = -1;
    private static final long DEFAULT_VALUE_LONG = -1L;
    private static final float DEFAULT_VALUE_FLOAT = -1F;
    //preference 불러오기
    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }
    //데이터 저장(String)
    public static void set(Context context, String key, String value) {
        SharedPreferences pref = getPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.apply();
    }
    //데이터 저장(Boolean)
    public static void set(Context context, String key, boolean value) {
        SharedPreferences pref = getPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key, value);
        editor.apply();
        editor.commit();
    }
    //데이터 저장(Int)
    public static void set(Context context, String key, int value) {
        SharedPreferences pref = getPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(key, value);
        editor.apply();
        editor.commit();
    }
    //데이터 저장(Long)
    public static void set(Context context, String key, long value) {
        SharedPreferences pref = getPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putLong(key, value);
        editor.apply();editor.commit();
    }
    //데이터 저장(Float)
    public static void set(Context context, String key, float value) {
        SharedPreferences pref = getPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putFloat(key, value);
        editor.apply();
        editor.commit();
    }
    //데이터 불러오기(String)
    public static String getString(Context context, String key) {
        SharedPreferences pref = getPreferences(context);
        String value = pref.getString(key, DEFAULT_VALUE_STRING);
        return value;
    }
    //데이터 불러오기(Boolean)
    public static boolean getBoolean(Context context, String key) {
        SharedPreferences pref = getPreferences(context);
        boolean value = pref.getBoolean(key, DEFAULT_VALUE_BOOLEAN);
        return value;
    }
    //데이터 불러오기(Int)
    public static int getInt(Context context, String key) {
        SharedPreferences pref = getPreferences(context);
        int value = pref.getInt(key, DEFAULT_VALUE_INT);
        return value;
    }
    //데이터 불러오기(Long)
    public static long getLong(Context context, String key) {
        SharedPreferences pref = getPreferences(context);
        long value = pref.getLong(key, DEFAULT_VALUE_LONG);
        return value;
    }
    //데이터 불러오기(Float)
    public static float getFloat(Context context, String key) {
        SharedPreferences pref = getPreferences(context);
        float value = pref.getFloat(key, DEFAULT_VALUE_FLOAT);
        return value;
    }

    //데이터 삭제
    public static void removeKey(Context context, String key) {
        SharedPreferences pref = getPreferences(context);
        SharedPreferences.Editor edit = pref.edit();
        edit.remove(key);
        edit.commit();
    }
    //전체 데이터 삭제
    public static void removeAll(Context context) {
        SharedPreferences pref = getPreferences(context);
        SharedPreferences.Editor edit = pref.edit();
        edit.clear();
        edit.commit();
    }
}
