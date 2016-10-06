package com.example.posted.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import com.example.posted.constants.ConstantsHelper;
import com.example.posted.database.DatabaseManager;
import com.example.posted.database.LaptopsDatabaseManager;
import com.example.posted.interfaces.Laptop;
import com.example.posted.models.LaptopKinvey;
import com.example.posted.models.LaptopSqlite;
import com.kinvey.android.AsyncAppData;
import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyDeleteCallback;
import com.kinvey.android.callback.KinveyListCallback;
import com.kinvey.java.Query;
import com.kinvey.java.User;
import com.kinvey.java.core.KinveyClientCallback;
import com.kinvey.java.model.KinveyDeleteResponse;

import java.io.File;
import java.util.ArrayList;


public class LoadDataService extends IntentService {

    private static final String APP_KEY = "kid_Hkz4aiD3";
    private static final String APP_SECRET = "6e30f9fd9c0b4218a6db8d6282ce25a8";
    private static final String COLLECTION_NAME = "laptops";
    private Client mKinveyClient;
    private IBinder binder;
    private DatabaseManager mController;
    private LaptopsDatabaseManager mLaptopsDatabaseManager;

    ///////////////////////////////////////////////////////////////////////////////
    public static final String BROADCAST_START_LOADING = "com.example.posted.start";
    public static final String BROADCAST_END_LOADING = "com.example.posted.end";

    public class LoadDataServiceBinder extends Binder {

        public LoadDataService getService() {
            return LoadDataService.this;
        }
    }


    public LoadDataService() {
        super("Download service");
    }


    @Override
    public IBinder onBind(Intent intent) {
        return this.binder;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "Service created", Toast.LENGTH_SHORT).show();
        this.binder = new LoadDataServiceBinder();
        this.mKinveyClient = new Client.Builder(APP_KEY, APP_SECRET, this.getApplicationContext()).build();
//        if (!doesDatabaseExist(getApplicationContext(),LaptopsDatabaseManager.DB_NAME)) {
//            this.mController = new LaptopsDatabaseManager(getApplicationContext());
//        }

        this.mController = new DatabaseManager(this.getApplicationContext());
        this.mLaptopsDatabaseManager = new LaptopsDatabaseManager(this.mController);
        this.loginToKinvey();
        this.transferDataFromKinvey();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Service started", Toast.LENGTH_SHORT).show();
        SQLiteDatabase mDatabase = this.mController.getWritableDatabase();
        this.mController.onCreate(mDatabase);
        return START_STICKY;
    }

    private void loginToKinvey() {
        if (!this.mKinveyClient.user().isUserLoggedIn()) {
            this.mKinveyClient.user().login("test@abv.bg", "test123", new KinveyClientCallback<User>() {
                @Override
                public void onSuccess(User user) {
                    Toast.makeText(LoadDataService.this, "Successfully logged in", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Throwable throwable) {
                    Toast.makeText(LoadDataService.this, "Fail to logged in", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(LoadDataService.this, "User already logged in", Toast.LENGTH_SHORT).show();
        }
    }

    public void transferDataFromKinvey() {
        /////////////////////////////////////////////////////////
        Intent startLoading = new Intent(BROADCAST_START_LOADING);
        sendBroadcast(startLoading);

        this.mController.deleteRecordsFromTable(ConstantsHelper.LAPTOPS_TABLE_NAME);
        AsyncAppData<LaptopKinvey> laptopsInfo = this.mKinveyClient.appData(COLLECTION_NAME, LaptopKinvey.class);
        laptopsInfo.get(new KinveyListCallback<LaptopKinvey>() {
            @Override
            public void onSuccess(LaptopKinvey[] laptops) {
                Toast.makeText(LoadDataService.this, "Successfully receive the info", Toast.LENGTH_SHORT).show();
                for (LaptopKinvey laptop : laptops) {
                    LoadDataService.this.mLaptopsDatabaseManager.insertRecord(laptop, ConstantsHelper
                            .LAPTOPS_TABLE_NAME);

                    ///////////////////////////////////////////////////////
                    Intent endLoading = new Intent(BROADCAST_END_LOADING);
                    sendBroadcast(endLoading);
                }

            }

            @Override
            public void onFailure(Throwable throwable) {
                Toast.makeText(LoadDataService.this, "Fail to receive the info", Toast.LENGTH_SHORT).show();

                ////////////////////////////////////////////////////////
                Intent endLoading = new Intent(BROADCAST_END_LOADING);
                sendBroadcast(endLoading);
            }

        });

    }

    public void uploadLaptops(ArrayList<LaptopSqlite> tempLaptops) {
        this.loginToKinvey();
        for (LaptopSqlite tempLaptop : tempLaptops) {
            LaptopKinvey laptopForUpload = new LaptopKinvey(
                    tempLaptop.getModel(),
                    tempLaptop.getCapacity_ram(),
                    tempLaptop.getCapacity_hdd(),
                    tempLaptop.getProcessor_type(),
                    tempLaptop.getVideo_card_type(),
                    tempLaptop.getDisplay_size(),
                    tempLaptop.getCurrency(),
                    tempLaptop.getPrice(),
                    tempLaptop.getImage());
            AsyncAppData<LaptopKinvey> laptopsInfo = this.mKinveyClient.appData(COLLECTION_NAME, LaptopKinvey.class);
            laptopsInfo.save(laptopForUpload, new KinveyClientCallback<LaptopKinvey>() {
                @Override
                public void onSuccess(LaptopKinvey laptopKinvey) {
                    Toast.makeText(LoadDataService.this, "Laptop " + laptopKinvey.getModel() + " Successfully " +
                            "uploaded", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Throwable throwable) {
                    Toast.makeText(LoadDataService.this, "Fail to upload laptop", Toast.LENGTH_SHORT).show();
                    Log.d("Service", throwable.getMessage());
                }
            });

        }

    }

    public void removeLaptopFromKinvey(final Laptop laptopToRemove){
        this.loginToKinvey();
        Query query = new Query();
        query.equals("model",laptopToRemove.getModel());
        AsyncAppData<LaptopKinvey> laptopsInfo = this.mKinveyClient.appData(COLLECTION_NAME, LaptopKinvey.class);
        laptopsInfo.delete(query, new KinveyDeleteCallback() {
            @Override
            public void onSuccess(KinveyDeleteResponse kinveyDeleteResponse) {
                Toast.makeText(LoadDataService.this, "Laptop " + laptopToRemove.getModel() + " Successfully " +
                        "deleted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Throwable throwable) {
                Toast.makeText(LoadDataService.this, "Fail to delete laptop", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service destroyed", Toast.LENGTH_SHORT).show();
        if (this.mKinveyClient != null) {
            this.mKinveyClient.user().logout().execute();
        }
        super.onDestroy();
    }

    private static boolean doesDatabaseExist(Context context, String dbName) {
        File dbFile = context.getDatabasePath(dbName);
        return dbFile.exists();
    }
}
