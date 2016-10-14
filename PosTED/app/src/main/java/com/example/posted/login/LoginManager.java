package com.example.posted.login;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.posted.database.DatabaseManager;
import com.example.posted.models.User;


public class LoginManager {

    private static final String PREFS_NAME = "TEST";
    private static final int PRIVATE_MODE = 0;
    private static final String USER_ID_KEY = "user id";
    private static final String LOGIN_KEY = "loggin";
    private static final String USERNAME_KEY = "username";
    private static final String USER_PASSWORD = "password";

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private DatabaseManager mDatabaseManager;

    public LoginManager(Context context) {
        this.mPreferences = context.getSharedPreferences(PREFS_NAME, PRIVATE_MODE);
        this.mEditor = this.mPreferences.edit();
        this.mDatabaseManager = new DatabaseManager(context);
    }

    public void loginUser(User user) {
        if (this.mPreferences != null && this.mEditor != null) {
            this.mEditor.putLong(USERNAME_KEY, user.getId());
            this.mEditor.putString(USERNAME_KEY, user.getUsername());
            this.mEditor.putString(USER_PASSWORD, user.getPassword());
            this.mEditor.putBoolean(LOGIN_KEY, true);
            this.mEditor.apply();
            this.mDatabaseManager.createCurrentOrderTable(this.mDatabaseManager.getWritableDatabase());
            this.mDatabaseManager.createAdminOrderTable(this.mDatabaseManager.getWritableDatabase());
        }
    }

    public void logoutUser() {
        if (this.mPreferences != null && this.mEditor != null) {
            this.mEditor.putString(USERNAME_KEY, "");
            this.mEditor.putString(USER_PASSWORD, "");
            this.mEditor.putBoolean(LOGIN_KEY, false);
            this.mEditor.apply();

            this.mDatabaseManager.dropCurrentOrderTable(this.mDatabaseManager.getWritableDatabase());
        }
    }

    public boolean isLoggedIn() {
        if (this.mPreferences == null) {
            return false;
        }

        return this.mPreferences.getBoolean(LOGIN_KEY, false);
    }

    public User getLoginUser() {
        Long userId = this.mPreferences.getLong(USER_ID_KEY, 0);
        String username = this.mPreferences.getString(USERNAME_KEY, "");
        String password = this.mPreferences.getString(USER_PASSWORD, "");

        return new User(userId, username, password);
    }
}
