package com.example.posted.login;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.posted.database.DatabaseManager;


public class LoginManager {

    private static final String PREFS_NAME = "TEST";
    private static final int PRIVATE_MODE = 0;
    private static final String USER_ID_KEY = "user id";
    private static final String LOGIN_KEY = "loggin";
    private static final String USERNAME_KEY = "username";
    private static final String USER_PASSWORD = "password";

    private SharedPreferences preferences;
    private SharedPreferences.Editor mEditor;
    private DatabaseManager databaseManager;

    public LoginManager(Context mContext) {
        this.preferences = mContext.getSharedPreferences(PREFS_NAME, PRIVATE_MODE);
        this.mEditor = this.preferences.edit();
        this.databaseManager = new DatabaseManager(mContext);
    }

    public void loginUser(User user) {
        if (this.preferences != null && this.mEditor != null) {
            this.mEditor.putLong(USERNAME_KEY, user.getId());
            this.mEditor.putString(USERNAME_KEY, user.getUsername());
            this.mEditor.putString(USER_PASSWORD, user.getPassword());
            this.mEditor.putBoolean(LOGIN_KEY, true);
            this.mEditor.apply();
            this.databaseManager.createCurrentOrderTable(this.databaseManager.getWritableDatabase());
        }
    }

    public void logoutUser() {
        if (this.preferences != null && this.mEditor != null) {
            this.mEditor.putString(USERNAME_KEY, "");
            this.mEditor.putString(USER_PASSWORD, "");
            this.mEditor.putBoolean(LOGIN_KEY, false);
            this.mEditor.apply();

            this.databaseManager.dropCurrentOrderTable(this.databaseManager.getWritableDatabase());
        }
    }

    public boolean isLoggedIn() {

        if (this.preferences == null) {
            return false;
        }

        return this.preferences.getBoolean(LOGIN_KEY, false);
    }

    public User getLoginUser() {
        Long userId = this.preferences.getLong(USER_ID_KEY, 0);
        String username = this.preferences.getString(USERNAME_KEY, "");
        String password = this.preferences.getString(USER_PASSWORD, "");

        return new User(userId, username, password);
    }
}
