package com.example.posted.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.example.posted.constants.ConstantsHelper;
import com.example.posted.models.LaptopSqlite;

import java.util.ArrayList;
import java.util.HashMap;

public class SqliteController extends SQLiteOpenHelper {


    public static final String TABLE_NAME = "laptops";
    public static final String ID_COLUMN = "id";
    public static final String MODEL_COLUMN = "model";
    public static final String RAM_COLUMN = "capacity_ram";
    public static final String HDD_COLUMN = "capacity_hdd";
    public static final String PROCESSOR_COLUMN = "processor_type";
    public static final String VIDEO_CARD_COLUMN = "video_card_type";
    public static final String DISPLAY_COLUMN = "display_size";
    public static final String CURRENCY_COLUMN = "currency";
    public static final String PRICE_COLUMN = "price";
    public static final String IMAGE_COLUMN = "image";

    private Context ctx;

    public SqliteController(Context context) {
        super(context, ConstantsHelper.DB_NAME, null, ConstantsHelper.DB_VERSION);
        this.ctx = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME +
                "( " + ID_COLUMN + " TEXT PRIMARY KEY, " +
                MODEL_COLUMN + " TEXT, " +
                RAM_COLUMN + " TEXT, " +
                HDD_COLUMN + " TEXT, " +
                PROCESSOR_COLUMN + " TEXT, " +
                VIDEO_CARD_COLUMN + " TEXT, " +
                DISPLAY_COLUMN + " TEXT, " +
                CURRENCY_COLUMN + " TEXT, " +
                PRICE_COLUMN + " TEXT, " +
                IMAGE_COLUMN + " TEXT )";
        db.execSQL(query);
        Toast.makeText(ctx, "Table created", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(query);
        onCreate(db);
    }

    public void dropTable(){
        SQLiteDatabase database = this.getWritableDatabase();
        String query = "DROP TABLE IF EXISTS " + TABLE_NAME;
        database.execSQL(query);

    }

    public void insertRecord(HashMap<String, String> queryValues) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ID_COLUMN, queryValues.get(ID_COLUMN));
        values.put(MODEL_COLUMN, queryValues.get(MODEL_COLUMN));
        values.put(RAM_COLUMN, queryValues.get(RAM_COLUMN));
        values.put(HDD_COLUMN, queryValues.get(HDD_COLUMN));
        values.put(PROCESSOR_COLUMN, queryValues.get(PROCESSOR_COLUMN));
        values.put(VIDEO_CARD_COLUMN, queryValues.get(VIDEO_CARD_COLUMN));
        values.put(DISPLAY_COLUMN, queryValues.get(DISPLAY_COLUMN));
        values.put(CURRENCY_COLUMN, queryValues.get(CURRENCY_COLUMN));
        values.put(PRICE_COLUMN, queryValues.get(PRICE_COLUMN));
        values.put(IMAGE_COLUMN, queryValues.get(IMAGE_COLUMN));

        database.insert(TABLE_NAME, null, values);
        database.close();
    }

    public ArrayList<LaptopSqlite> getAllStudents(){
        ArrayList<LaptopSqlite> result = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                LaptopSqlite current = new LaptopSqlite();
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
}
