package com.snapit.genia.snapit.utils;
import android.content.Context;
import android.content.SharedPreferences;
/**
 * Created by Asierae on 09/03/2018.
 */


public class PrefManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "snap-login";

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String IS_FIRST_TIME_SECRET = "IsFirstTimeSecret";
    private static final String APIKEY = "apikey";
    private static final String REQUESTKEY = "requestkey";
    private static final String USERNAME = "u";
    private static final String PASS = "p";

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }
    public void setAPIkey(String apikey) {
        editor.putString(APIKEY, apikey);
        editor.commit();
    }
    public  String getAPIkey(){
        return pref.getString(APIKEY,"");
    }

    public void setRequestkey(String rkey) {
        editor.putString(REQUESTKEY, rkey);
        editor.commit();
    }
    public  String getRequestkey(){
        return pref.getString(REQUESTKEY,"");
    }
    public void setUsername(String u){
        editor.putString(USERNAME, u);
        editor.commit();
    }

    public  String getPass(){
        return pref.getString(PASS,"");
    }

    public void setPass(String p){
        editor.putString(PASS, p);
        editor.commit();
    }

    public  String getUsername(){
        return pref.getString(USERNAME,"");
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public boolean isFirstTimeSecret() {
        return pref.getBoolean(IS_FIRST_TIME_SECRET, true);
    }
    public void setFirstTimeSecret(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_SECRET, isFirstTime);
        editor.commit();
    }

}