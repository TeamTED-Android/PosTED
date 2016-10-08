package com.example.posted.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.posted.constants.ConstantsHelper;
import com.example.posted.login.User;


public class UsersDatabaseManager {

    private DatabaseManager mDatabaseManager;

    public UsersDatabaseManager(DatabaseManager databaseManager) {
        this.mDatabaseManager = databaseManager;
    }

    public User insertUser(User queryValues) {
        SQLiteDatabase database = this.mDatabaseManager.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ConstantsHelper.USERNAME_COLUMN, queryValues.getUsername());
        values.put(ConstantsHelper.PASSWORD_COLUMN, queryValues.getPassword());
        queryValues.setId(database.insert(ConstantsHelper.USERS_TABLE_NAME, null, values));
        database.close();
        return queryValues;
    }

    public void updateUserPassword(User queryValues) {
        SQLiteDatabase database = this.mDatabaseManager.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", queryValues.getUsername());
        values.put("password", queryValues.getPassword());
        queryValues.setId(database.insert(ConstantsHelper.USERS_TABLE_NAME, null, values));

        database.update(ConstantsHelper.USERS_TABLE_NAME, values, ConstantsHelper.ID_COLUMN + " = ?", new
                String[]{String.valueOf(queryValues.getId())});
        database.close();
    }

    public User getUser(String username) {
        String query = "SELECT * FROM "
                + ConstantsHelper.USERS_TABLE_NAME
                + " WHERE " + ConstantsHelper.USERNAME_COLUMN + " ='"
                + username + "'";
        User myUser = new User(0, username, "");
        SQLiteDatabase database = this.mDatabaseManager.getReadableDatabase();
        Cursor cursor = database.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                myUser.setId(cursor.getLong(0));
                myUser.setPassword(cursor.getString(2));
            } while (cursor.moveToNext());
        }
        return myUser;
    }
}
