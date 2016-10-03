package com.example.posted.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.posted.constants.ConstantsHelper;
import com.example.posted.models.LaptopSqlite;

import java.util.ArrayList;
import java.util.HashMap;

public class LaptopsDatabaseManager {


   private DatabaseManager databaseManager;

    public LaptopsDatabaseManager(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void insertRecord(HashMap<String, String> queryValues, String tableName) {
        SQLiteDatabase database = this.databaseManager.getWritableDatabase();
        ContentValues values = new ContentValues();
       // values.put(ConstantsHelper.ID_COLUMN, queryValues.get(ConstantsHelper.ID_COLUMN));
        values.put(ConstantsHelper.MODEL_COLUMN, queryValues.get(ConstantsHelper.MODEL_COLUMN));
        values.put(ConstantsHelper.RAM_COLUMN, queryValues.get(ConstantsHelper.RAM_COLUMN));
        values.put(ConstantsHelper.HDD_COLUMN, queryValues.get(ConstantsHelper.HDD_COLUMN));
        values.put(ConstantsHelper.PROCESSOR_COLUMN, queryValues.get(ConstantsHelper.PROCESSOR_COLUMN));
        values.put(ConstantsHelper.VIDEO_CARD_COLUMN, queryValues.get(ConstantsHelper.VIDEO_CARD_COLUMN));
        values.put(ConstantsHelper.DISPLAY_COLUMN, queryValues.get(ConstantsHelper.DISPLAY_COLUMN));
        values.put(ConstantsHelper.CURRENCY_COLUMN, queryValues.get(ConstantsHelper.CURRENCY_COLUMN));
        values.put(ConstantsHelper.PRICE_COLUMN, queryValues.get(ConstantsHelper.PRICE_COLUMN));
        values.put(ConstantsHelper.IMAGE_COLUMN, queryValues.get(ConstantsHelper.IMAGE_COLUMN));

        database.insert(tableName, null, values);
        database.close();
    }

    public ArrayList<LaptopSqlite> getAllLaptops(String tableName){
        ArrayList<LaptopSqlite> result = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + tableName;
        SQLiteDatabase database = this.databaseManager.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                LaptopSqlite current = new LaptopSqlite();
                current.setId(cursor.getInt(0));
                current.setModel(cursor.getString(1));
                current.setCapacity_ram(cursor.getString(2));
                current.setCapacity_hdd(cursor.getString(3));
                current.setProcessor_type(cursor.getString(4));
                current.setVideo_card_type(cursor.getString(5));
                current.setDisplay_size(cursor.getString(6));
                current.setCurrency(cursor.getString(7));
                current.setPrice(cursor.getString(8));
                current.setImage(cursor.getString(9));
                result.add(current);
            } while (cursor.moveToNext());

        }
        if (cursor != null && !cursor.isClosed()){
            cursor.close();
        }
        database.close();
        return result;
    }

    //Using temporary table for all records from Admin...After that upload all new records in Kinvey and drop the temporary table
    public void createTempTable() {
        SQLiteDatabase database = this.databaseManager.getWritableDatabase();
        this.databaseManager.createTempLaptopTable(database);
    }
}
