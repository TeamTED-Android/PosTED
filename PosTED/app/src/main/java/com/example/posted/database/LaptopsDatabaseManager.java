package com.example.posted.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.posted.constants.ConstantsHelper;
import com.example.posted.interfaces.Laptop;
import com.example.posted.models.LaptopSqlite;

import java.util.ArrayList;

public class LaptopsDatabaseManager {

    private DatabaseManager databaseManager;

    public LaptopsDatabaseManager(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void insertRecord(Laptop currentLaptop, String tableName) {
        SQLiteDatabase database = this.databaseManager.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ConstantsHelper.MODEL_COLUMN, currentLaptop.getModel());
        values.put(ConstantsHelper.RAM_COLUMN, currentLaptop.getCapacity_ram());
        values.put(ConstantsHelper.HDD_COLUMN, currentLaptop.getCapacity_hdd());
        values.put(ConstantsHelper.PROCESSOR_COLUMN, currentLaptop.getProcessor_type());
        values.put(ConstantsHelper.VIDEO_CARD_COLUMN, currentLaptop.getVideo_card_type());
        values.put(ConstantsHelper.DISPLAY_COLUMN, currentLaptop.getDisplay_size());
        values.put(ConstantsHelper.CURRENCY_COLUMN, currentLaptop.getCurrency());
        values.put(ConstantsHelper.PRICE_COLUMN, currentLaptop.getPrice());
        values.put(ConstantsHelper.IMAGE_COLUMN, currentLaptop.getImage());

        database.insert(tableName, null, values);
        database.close();
    }

    public ArrayList<LaptopSqlite> getAllLaptops(String tableName) {
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
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        database.close();
        return result;
    }

    // Using temporary table for all records from Admin...
    // After that upload all new records in Kinvey and drop the temporary table
    public void createTempTable() {
        SQLiteDatabase database = this.databaseManager.getWritableDatabase();
        this.databaseManager.createTempLaptopTable(database);
    }

    public void deleteRecord(Laptop currentLaptop, String tableName) {
        SQLiteDatabase database = this.databaseManager.getWritableDatabase();
        LaptopSqlite laptopSqlite = (LaptopSqlite) currentLaptop;
        database.delete(tableName, "id=?", new String[]{Integer.toString(laptopSqlite.getId())});
    }
}
