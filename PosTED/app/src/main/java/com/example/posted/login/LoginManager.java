package com.example.posted.login;

import android.content.Context;
import android.content.SharedPreferences;



public class LoginManager {
    private static final String PREFS_NAME = "TEST";
    private static final int PRIVATE_MODE = 0;
    private static final String LOGIN_KEY = "loggin";
    private static final String USERNAME_KEY = "username";
    private static final String USER_PASSWORD = "password";

    private Context mContext;
    private SharedPreferences preferences;
    private SharedPreferences.Editor mEditor;

    public LoginManager(Context mContext) {
        this.mContext = mContext;
        this.preferences = mContext.getSharedPreferences(PREFS_NAME,PRIVATE_MODE);
        this.mEditor = this.preferences.edit();
    }

    public void loginUser(User user){
        if (preferences != null && mEditor != null){
            this.mEditor.putString(USERNAME_KEY,user.getUsername());
            this.mEditor.putString(USER_PASSWORD,user.getPassword());
            this.mEditor.putBoolean(LOGIN_KEY,true);
            this.mEditor.apply();
        }
    }

    public void logoutUser(){
        if (preferences != null && mEditor != null){
            this.mEditor.putString(USERNAME_KEY,"");
            this.mEditor.putString(USER_PASSWORD,"");
            this.mEditor.putBoolean(LOGIN_KEY,false);
            this.mEditor.apply();
        }
    }

    public boolean isLoggedIn(){

        if (this.preferences == null ){
            return false;
        }

        return this.preferences.getBoolean(LOGIN_KEY,false);
    }
}
