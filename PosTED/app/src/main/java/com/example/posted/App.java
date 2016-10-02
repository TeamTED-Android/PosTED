package com.example.posted;

import android.app.Application;
import android.app.PendingIntent;
import android.content.Intent;

import com.example.posted.adminApp.AdminMainActivity;
import com.example.posted.constants.ConstantsHelper;
import com.example.posted.login.LoginActivity;
import com.example.posted.login.LoginManager;
import com.example.posted.login.User;

public class App extends Application {
    private LoginManager loginManager;
    private Intent intent;

    @Override
    public void onCreate() {
        super.onCreate();
        this.loginManager = new LoginManager(this);
        if (loginManager.isLoggedIn()) {
            User currentUser = loginManager.getLoginUser();
            if (currentUser.getUsername().equalsIgnoreCase(ConstantsHelper.ADMIN_USERNAME)){
                intent = new Intent(this, AdminMainActivity.class);
            } else {
                intent = new Intent(this, MainActivity.class);
            }

        } else {
            intent = new Intent(this, LoginActivity.class);
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 22, intent, 0);
        try {
            pendingIntent.send();
        } catch (PendingIntent.CanceledException e) {


            e.printStackTrace();
        }
    }
}
