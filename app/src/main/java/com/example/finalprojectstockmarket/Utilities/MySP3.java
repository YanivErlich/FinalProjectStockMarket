package com.example.finalprojectstockmarket.Utilities;

import android.content.Context;
import android.content.SharedPreferences;

public class MySP3 {

    private static final String DB_FILE = "DB_FILE";

    private static MySP3 instance = null;
    private SharedPreferences sharedPreferences;

    private MySP3(Context context) {
        sharedPreferences = context.getSharedPreferences(DB_FILE, Context.MODE_PRIVATE);
    }

    public static void init(Context context){
        if (instance == null){
            instance = new MySP3(context);
        }
    }

    public static MySP3 getInstance() {
        return instance;
    }

    public String getString(String key, String value) {

        return sharedPreferences.getString(key, value);
    }

    public void putString(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public int getInt(String key, int value) {
        return sharedPreferences.getInt(key, value);
    }

    public void putInt(String key, int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }
}
