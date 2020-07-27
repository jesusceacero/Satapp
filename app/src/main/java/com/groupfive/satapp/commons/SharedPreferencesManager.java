package com.groupfive.satapp.commons;

import android.content.Context;
import android.content.SharedPreferences;

import com.groupfive.satapp.commons.Constants;
import com.groupfive.satapp.commons.MyApp;

public class SharedPreferencesManager {

    private Context ctx;
    private SharedPreferencesManager(Context ctx){}

    private static SharedPreferences getSharedPreferences(){
        return MyApp.getContext().getSharedPreferences(Constants.APP_SETTINGS_FILE, Context.MODE_PRIVATE);
    }

    public static void setStringValue(String dataLabel, String dataValue){
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(dataLabel,dataValue);
        editor.commit();
    }

    public static String getStringValue(String dataLabel){
        return getSharedPreferences().getString(dataLabel,null);
    }
}
