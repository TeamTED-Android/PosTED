package com.example.posted.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.posted.constants.ConstantsHelper;

public class DatabaseManager extends SQLiteOpenHelper {


    public DatabaseManager(Context context) {
        super(context, ConstantsHelper.DB_NAME, null, ConstantsHelper.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String usersTableQuery = "CREATE TABLE IF NOT EXISTS "
                + ConstantsHelper.USERS_TABLE_NAME
                + " ("
                + ConstantsHelper.ID_COLUMN
                + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ConstantsHelper.USERNAME_COLUMN + " TEXT, "
                + ConstantsHelper.PASSWORD_COLUMN + " TEXT)";

        String query = "CREATE TABLE IF NOT EXISTS " +
                ConstantsHelper.LAPTOPS_TABLE_NAME +
                "( " + ConstantsHelper.ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ConstantsHelper.MODEL_COLUMN + " TEXT, " +
                ConstantsHelper.RAM_COLUMN + " TEXT, " +
                ConstantsHelper.HDD_COLUMN + " TEXT, " +
                ConstantsHelper.PROCESSOR_COLUMN + " TEXT, " +
                ConstantsHelper.VIDEO_CARD_COLUMN + " TEXT, " +
                ConstantsHelper.DISPLAY_COLUMN + " TEXT, " +
                ConstantsHelper.CURRENCY_COLUMN + " TEXT, " +
                ConstantsHelper.PRICE_COLUMN + " TEXT, " +
                ConstantsHelper.IMAGE_COLUMN + " MEDIUMTEXT )";
        db.execSQL(query);


        db.execSQL(usersTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query = "DROP TABLE IF EXISTS " + ConstantsHelper.LAPTOPS_TABLE_NAME;
        db.execSQL(query);
        onCreate(db);
    }

    public void dropLaptopsTable(){
        SQLiteDatabase database = this.getWritableDatabase();
        String query = "DROP TABLE IF EXISTS " + ConstantsHelper.LAPTOPS_TABLE_NAME;
        database.execSQL(query);

    }

    public void dropTempTable(){
        SQLiteDatabase database = this.getWritableDatabase();
        String query = "DROP TABLE IF EXISTS " + ConstantsHelper.TEMP_LAPTOPS_TABLE_NAME;
        database.execSQL(query);

    }

    //Create Temporary table for all records from Admin panel
    public void createTempLaptopTable(SQLiteDatabase db) {
        String query = "CREATE TABLE IF NOT EXISTS " +
                ConstantsHelper.TEMP_LAPTOPS_TABLE_NAME +
                "( " + ConstantsHelper.ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ConstantsHelper.MODEL_COLUMN + " TEXT, " +
                ConstantsHelper.RAM_COLUMN + " TEXT, " +
                ConstantsHelper.HDD_COLUMN + " TEXT, " +
                ConstantsHelper.PROCESSOR_COLUMN + " TEXT, " +
                ConstantsHelper.VIDEO_CARD_COLUMN + " TEXT, " +
                ConstantsHelper.DISPLAY_COLUMN + " TEXT, " +
                ConstantsHelper.CURRENCY_COLUMN + " TEXT, " +
                ConstantsHelper.PRICE_COLUMN + " TEXT, " +
                ConstantsHelper.IMAGE_COLUMN + " MEDIUMTEXT )";
        db.execSQL(query);
    }
}
