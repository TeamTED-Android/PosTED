package com.example.posted.login;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.example.posted.constants.ConstantsHelper;


public class DBTools extends SQLiteOpenHelper {
    private static final String TABLE_NAME = "users";
    Context context;
    public DBTools(Context context) {
        super(context, ConstantsHelper.DB_NAME, null, ConstantsHelper.DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME+" (userId INTEGER PRIMARY KEY AUTOINCREMENT, "+
                " username TEXT, password TEXT)";
        db.execSQL(query);
        Toast.makeText(context,"Users created",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(query);
        onCreate(db);
    }

    public User insertUser (User queryValues){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", queryValues.getUsername());
        values.put("password", queryValues.getPassword());
        queryValues.setId(database.insert(TABLE_NAME, null, values));
        database.close();
        return queryValues;
    }

    public int updateUserPassword (User queryValues){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", queryValues.getUsername());
        values.put("password", queryValues.getPassword());
        queryValues.setId(database.insert(TABLE_NAME, null, values));
        database.close();
        return database.update(TABLE_NAME, values, "userId = ?", new String[] {String.valueOf(queryValues.getId())});
    }

    public User getUser (String username){
        String query = "SELECT userId, password FROM " + TABLE_NAME +" WHERE username ='"+username+"'";
        User myUser = new User(0,username,"");
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(query, null);
        if (cursor.moveToFirst()){
            do {
                myUser.setId(cursor.getLong(0));
                myUser.setPassword(cursor.getString(1));
            } while (cursor.moveToNext());
        }
        return myUser;
    }
}
