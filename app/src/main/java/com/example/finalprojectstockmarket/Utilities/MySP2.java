package com.example.finalprojectstockmarket.Utilities;


public class MySP2 {

    public static final String SHARED_PREFS_FILE = "mypref";
    public static final String FAVOURITES = "favourites";
    public static final String PORTFOLIO = "portfolio";


    private static android.content.SharedPreferences sharedPreferences;
    private static android.content.SharedPreferences.Editor editor;


    public MySP2(android.content.SharedPreferences sharedPreferences, android.content.SharedPreferences.Editor editor){
        this.sharedPreferences = sharedPreferences;
        this.editor = editor;

    }



}
