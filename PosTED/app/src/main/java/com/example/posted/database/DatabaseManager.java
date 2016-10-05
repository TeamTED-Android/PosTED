package com.example.posted.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;
import com.example.posted.constants.ConstantsHelper;

public class DatabaseManager extends SQLiteOpenHelper {

    private Context mContext;

    public DatabaseManager(Context context) {
        super(context, ConstantsHelper.DB_NAME, null, ConstantsHelper.DB_VERSION);
        this.mContext = context;
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
        this.onCreate(db);
    }

    public void deleteRecordsFromTable(String tableName) {
        SQLiteDatabase database = this.getWritableDatabase();
        Toast.makeText(this.mContext, tableName + " deleted", Toast.LENGTH_SHORT).show();
        String query = "DELETE FROM " + tableName;
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
        Toast.makeText(this.mContext, "TEMP table created", Toast.LENGTH_SHORT).show();
        db.execSQL(query);
    }

    public void createCurrentOrderTable(SQLiteDatabase db) {

        String query = "CREATE TABLE IF NOT EXISTS " +
                ConstantsHelper.CURRENT_ORDERS_LAPTOPS_TABLE_NAME +
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
        Toast.makeText(this.mContext, "Orders table created", Toast.LENGTH_SHORT).show();
        db.execSQL(query);
    }

    public void dropCurrentOrderTable(SQLiteDatabase db) {
        String query = "DROP TABLE IF EXISTS " + ConstantsHelper.CURRENT_ORDERS_LAPTOPS_TABLE_NAME;
        Toast.makeText(this.mContext, "Orders table dropped", Toast.LENGTH_SHORT).show();
        db.execSQL(query);
    }
}
