package com.bytecodesolutions.ugvclrecovery.util;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {
    SharedPreferences pref;
    public PrefManager(Context context,String pref_file_name){
        pref=context.getSharedPreferences(pref_file_name,Context.MODE_PRIVATE);

    }
    public void putIntValue(String key,int value){
        SharedPreferences.Editor editor=pref.edit();
        editor.putInt(key,value);
        editor.apply();

    }
    public int getIntValue(String key){
        return pref.getInt(key,0);

    }
    public void putLongValue(String key,long value){
        SharedPreferences.Editor editor=pref.edit();
        editor.putLong(key,value);
        editor.apply();

    }
    public long getLongValue(String key){
        return pref.getLong(key,-1);
    }

    public void putStringValue(String key,String value){
        SharedPreferences.Editor editor=pref.edit();
        editor.putString(key,value);
        editor.apply();

    }
    public String getStringValue(String key){
        return pref.getString(key,"");
    }
    public void putBooleanValue(String key,boolean value){
        SharedPreferences.Editor editor=pref.edit();
        editor.putBoolean(key,value);
        editor.apply();

    }
    public boolean getBooleanValue(String key){
        return pref.getBoolean(key,false);
    }
}
