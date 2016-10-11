package com.example.posted.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import com.example.posted.async.AsyncImageSaver;
import com.example.posted.constants.ConstantsHelper;
import com.example.posted.interfaces.Laptop;
import com.example.posted.models.LaptopSqlite;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class LaptopsDatabaseManager implements AsyncImageSaver.Listener {

    private DatabaseManager mDatabaseManager;
    private Context mContext;
//    private String mFullPathToImage;
//    private Laptop mKinveyCurrentLaptop;

    public LaptopsDatabaseManager(DatabaseManager databaseManager) {
        this.mDatabaseManager = databaseManager;
        this.mContext = this.mDatabaseManager.getContext();
    }

//    public void insertRecord(Laptop currentLaptop, String tableName) {
//        this.mCurrentLaptop = currentLaptop;
//        if (currentLaptop instanceof LaptopKinvey) {
//            AsyncImageDecoder decoder = new AsyncImageDecoder(this);
//            decoder.execute(currentLaptop.getImagePath());
//        } else {
//            this.mFullPathToImage = currentLaptop.getImagePath() + "/" + currentLaptop.getImageName();
//        }
//        AsyncImageSaver saver = new AsyncImageSaver(this, this.mContext);
//        saver.execute(currentLaptop.getImagePath(), currentLaptop.getImageName());
//    }

    public void insertLaptopIntoTable(Laptop sqliteLaptop, String tableName) {
        SQLiteDatabase database = this.mDatabaseManager.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ConstantsHelper.MODEL_COLUMN, sqliteLaptop.getModel());
        values.put(ConstantsHelper.RAM_COLUMN, sqliteLaptop.getCapacity_ram());
        values.put(ConstantsHelper.HDD_COLUMN, sqliteLaptop.getCapacity_hdd());
        values.put(ConstantsHelper.PROCESSOR_COLUMN, sqliteLaptop.getProcessor_type());
        values.put(ConstantsHelper.VIDEO_CARD_COLUMN, sqliteLaptop.getVideo_card_type());
        values.put(ConstantsHelper.DISPLAY_COLUMN, sqliteLaptop.getDisplay_size());
        values.put(ConstantsHelper.CURRENCY_COLUMN, sqliteLaptop.getCurrency());
        values.put(ConstantsHelper.PRICE_COLUMN, sqliteLaptop.getPrice());
        values.put(ConstantsHelper.IMAGE_PATH_COLUMN, sqliteLaptop.getImagePath());
        values.put(ConstantsHelper.IMAGE_NAME_COLUMN, sqliteLaptop.getImageName());

        database.insert(tableName, null, values);
        database.close();
    }

    public ArrayList<LaptopSqlite> getAllLaptops(String tableName) {
        ArrayList<LaptopSqlite> result = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + tableName;
        SQLiteDatabase database = this.mDatabaseManager.getReadableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
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
            current.setImagePath(cursor.getString(9));
            current.setImageName(cursor.getString(10));
            result.add(current);
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        database.close();
        return result;
    }

    // Using temporary table for all records from Admin...
    // After that upload all new records in Kinvey and drop the temporary table
    public void createTempTable() {
        SQLiteDatabase database = this.mDatabaseManager.getWritableDatabase();
        this.mDatabaseManager.createTempLaptopTable(database);
    }

    public void deleteRecord(Laptop currentLaptop, String tableName) {
        SQLiteDatabase database = this.mDatabaseManager.getWritableDatabase();
        LaptopSqlite laptopSqlite = (LaptopSqlite) currentLaptop;
        database.delete(tableName, "id=?", new String[]{Integer.toString(laptopSqlite.getId())});
    }

    public int getRecordCount(String tableName) {
        SQLiteDatabase database = this.mDatabaseManager.getReadableDatabase();
        String rawSQL = "SELECT * FROM " + tableName;
        Cursor allItemsCursor = database.rawQuery(rawSQL, null);
        int count = 0;
        if (allItemsCursor == null) {
            database.close();
            return count;
        }
        count = allItemsCursor.getCount();
        allItemsCursor.close();
        database.close();
        return count;
    }

    public void insertIntoMainDatabase(Laptop kinveyLaptop) {
        ContextWrapper cw = new ContextWrapper(this.mContext);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir(ConstantsHelper.IMAGE_DIRECTORY_PATH, Context.MODE_PRIVATE);
        // Create imageDir
        File file = new File(directory, kinveyLaptop.getImageName());
        if (!file.exists()) {
            AsyncImageSaver decoder = new AsyncImageSaver(this, kinveyLaptop);
            String base64Str = kinveyLaptop.getImagePath();
            decoder.execute(base64Str);
        } else {
            //check the else statement if the object is already in database or only the image exists
            this.insertKinveyLaptop(kinveyLaptop, directory.getAbsolutePath());
        }
    }

    @Override
    public void onImageSaved(Bitmap bitmap, Laptop laptop) {
        String imagePath = this.saveToInternalStorage(bitmap, laptop.getImageName());
        this.insertKinveyLaptop(laptop, imagePath);
    }

    private void insertKinveyLaptop(Laptop laptop, String imagePath) {
        SQLiteDatabase database = this.mDatabaseManager.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ConstantsHelper.MODEL_COLUMN, laptop.getModel());
        values.put(ConstantsHelper.RAM_COLUMN, laptop.getCapacity_ram());
        values.put(ConstantsHelper.HDD_COLUMN, laptop.getCapacity_hdd());
        values.put(ConstantsHelper.PROCESSOR_COLUMN, laptop.getProcessor_type());
        values.put(ConstantsHelper.VIDEO_CARD_COLUMN, laptop.getVideo_card_type());
        values.put(ConstantsHelper.DISPLAY_COLUMN, laptop.getDisplay_size());
        values.put(ConstantsHelper.CURRENCY_COLUMN, laptop.getCurrency());
        values.put(ConstantsHelper.PRICE_COLUMN, laptop.getPrice());
        values.put(ConstantsHelper.IMAGE_PATH_COLUMN, imagePath);
        values.put(ConstantsHelper.IMAGE_NAME_COLUMN, laptop.getImageName());

        database.insert(ConstantsHelper.LAPTOPS_TABLE_NAME, null, values);
        database.close();
    }

    private String saveToInternalStorage(final Bitmap bitmapImage, String imageName) {
        if (bitmapImage == null) {
            return ConstantsHelper.NO_IMAGE_TAG;
        }
        ContextWrapper cw = new ContextWrapper(this.mContext);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir(ConstantsHelper.IMAGE_DIRECTORY_PATH, Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory, imageName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }
}
