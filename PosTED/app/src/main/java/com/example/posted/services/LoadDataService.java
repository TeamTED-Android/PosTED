package com.example.posted.services;

import android.app.IntentService;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import com.example.posted.R;
import com.example.posted.async.AsyncImageEncoder;
import com.example.posted.constants.ConstantsHelper;
import com.example.posted.database.DatabaseManager;
import com.example.posted.database.LaptopsDatabaseManager;
import com.example.posted.interfaces.Laptop;
import com.example.posted.models.LaptopKinvey;
import com.example.posted.models.LaptopSqlite;
import com.example.posted.models.Order;
import com.kinvey.android.AsyncAppData;
import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyDeleteCallback;
import com.kinvey.android.callback.KinveyListCallback;
import com.kinvey.java.Query;
import com.kinvey.java.User;
import com.kinvey.java.core.KinveyClientCallback;
import com.kinvey.java.model.KinveyDeleteResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;


public class LoadDataService extends IntentService implements AsyncImageEncoder.Listener {

    private static final String APP_KEY = "kid_Hkz4aiD3";
    private static final String APP_SECRET = "6e30f9fd9c0b4218a6db8d6282ce25a8";
    private static final String KINVEY_USERNAME = "test@abv.bg";
    private static final String KINVEY_PASSWORD = "test123";
    private Client mKinveyClient;
    private IBinder mBinder;
    private DatabaseManager mController;
    private LaptopsDatabaseManager mLaptopsDatabaseManager;

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
        return this.mBinder;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }

    @Override
    public void onCreate() {
        // Toast.makeText(this, "Service created", Toast.LENGTH_SHORT).show();
        this.mBinder = new LoadDataServiceBinder();
        this.mKinveyClient = new Client.Builder(APP_KEY, APP_SECRET, this.getApplicationContext()).build();

        this.mController = new DatabaseManager(this.getApplicationContext());
        this.mLaptopsDatabaseManager = new LaptopsDatabaseManager(this.mController);
        this.loginToKinvey();
        this.transferDataFromKinvey();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Toast.makeText(this, "Service started", Toast.LENGTH_SHORT).show();
        SQLiteDatabase mDatabase = this.mController.getWritableDatabase();
        this.mController.onCreate(mDatabase);
        return START_STICKY;
    }

    private void loginToKinvey() {
        if (!this.mKinveyClient.user().isUserLoggedIn()) {
            this.mKinveyClient.user().login(KINVEY_USERNAME, KINVEY_PASSWORD, new KinveyClientCallback<User>() {
                @Override
                public void onSuccess(User user) {
                    // Toast.makeText(LoadDataService.this, "Successfully logged in", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Throwable throwable) {
                    //  Toast.makeText(LoadDataService.this, "Fail to logged in", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Toast.makeText(LoadDataService.this, "User already logged in", Toast.LENGTH_SHORT).show();
        }
    }

    public void transferDataFromKinvey() {
        Intent startLoading = new Intent(ConstantsHelper.BROADCAST_START_LOADING);
        this.sendBroadcast(startLoading);

        AsyncAppData<LaptopKinvey> laptopsInfo = this.mKinveyClient.appData(ConstantsHelper.KINVEY_LAPTOPS_TABLE_NAME, LaptopKinvey.class);
        laptopsInfo.get(new KinveyListCallback<LaptopKinvey>() {
            @Override
            public void onSuccess(LaptopKinvey[] laptops) {

                for (LaptopKinvey laptop : laptops) {
                    LoadDataService.this.mLaptopsDatabaseManager.insertIntoMainDatabase(laptop);
                    Intent endLoading = new Intent(ConstantsHelper.BROADCAST_END_LOADING);
                    LoadDataService.this.sendBroadcast(endLoading);
                }
                Toast.makeText(LoadDataService.this, "Successfully receive the info", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Throwable throwable) {
                Toast.makeText(LoadDataService.this, "Fail to receive the info", Toast.LENGTH_SHORT).show();
                Intent endLoading = new Intent(ConstantsHelper.BROADCAST_END_LOADING);
                LoadDataService.this.sendBroadcast(endLoading);
            }

        });
    }

    public void uploadLaptops(ArrayList<LaptopSqlite> laptopsToAdd) {
        if (laptopsToAdd.size() > 0) {
            Intent startLoading = new Intent(ConstantsHelper.BROADCAST_START_LOADING);
            this.sendBroadcast(startLoading);

            this.loginToKinvey();
            for (LaptopSqlite current : laptopsToAdd) {
                Bitmap bitmap = this.loadImage(current.getImagePath(), current.getImageName());
                AsyncImageEncoder encoder = new AsyncImageEncoder(this, current);
                encoder.execute(bitmap);
            }
        }
    }

    private Bitmap loadImage(String imgPath, String imgName) {
        if (imgPath == null || imgPath.equals("")) {
            return null;
        }

        Bitmap bitmap = null;
        try {
            File file = new File(imgPath, imgName);
            bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private void uploadKinveyLaptop(final Laptop tempLaptop, String base64Str) {
        File imgToDelete = new File(tempLaptop.getImagePath(), tempLaptop.getImageName());
        final LaptopKinvey laptopForUpload = new LaptopKinvey(
                tempLaptop.getModel(),
                tempLaptop.getCapacity_ram(),
                tempLaptop.getCapacity_hdd(),
                tempLaptop.getProcessor_type(),
                tempLaptop.getVideo_card_type(),
                tempLaptop.getDisplay_size(),
                tempLaptop.getCurrency(),
                tempLaptop.getPrice(),
                base64Str);
        AsyncAppData<LaptopKinvey> laptopsInfo = this.mKinveyClient.appData(ConstantsHelper.KINVEY_LAPTOPS_TABLE_NAME, LaptopKinvey.class);
        laptopsInfo.save(laptopForUpload, new KinveyClientCallback<LaptopKinvey>() {
            @Override
            public void onSuccess(LaptopKinvey laptopKinvey) {
//                Toast.makeText(LoadDataService.this, "Laptop " + laptopKinvey.getModel() + " Successfully " +
//                        "uploaded", Toast.LENGTH_SHORT).show();
                LoadDataService.this.mLaptopsDatabaseManager.deleteRecord(tempLaptop.getId(), ConstantsHelper.ADMIN_ADDED_LAPTOPS_TABLE_NAME);
                Intent endLoading = new Intent(ConstantsHelper.BROADCAST_END_LOADING);
                LoadDataService.this.sendBroadcast(endLoading);

                LoadDataService.this.transferDataFromKinvey();
            }

            @Override
            public void onFailure(Throwable throwable) {
                //  Toast.makeText(LoadDataService.this, "Fail to upload laptop", Toast.LENGTH_SHORT).show();
                Intent endLoading = new Intent(ConstantsHelper.BROADCAST_END_LOADING);
                LoadDataService.this.sendBroadcast(endLoading);
            }
        });
        if (!tempLaptop.getImagePath().equals("")) {
            boolean isDeleted = imgToDelete.delete();
        }

        // Toast.makeText(this, "File " + imgToDelete.getName() + " isDeleted " + isDeleted, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onImageEncoded(String base64str, Laptop laptop) {
        // Toast.makeText(LoadDataService.this, "Image encoded!", Toast.LENGTH_SHORT).show();
        this.uploadKinveyLaptop(laptop, base64str);
    }

    public void removeLaptops(ArrayList<LaptopSqlite> laptopsToRemove) {
        if (laptopsToRemove.size() > 0) {
            Intent startLoading = new Intent(ConstantsHelper.BROADCAST_START_LOADING);
            this.sendBroadcast(startLoading);

            this.loginToKinvey();
            for (LaptopSqlite current : laptopsToRemove) {
                this.removeLaptopFromKinvey(current);
            }
        }
    }

    private void removeLaptopFromKinvey(final Laptop laptopToRemove) {
        Query query = new Query();
        query.equals("_id", laptopToRemove.getId());
        AsyncAppData<LaptopKinvey> laptopsInfo = this.mKinveyClient.appData(ConstantsHelper.KINVEY_LAPTOPS_TABLE_NAME, LaptopKinvey.class);
        laptopsInfo.delete(query, new KinveyDeleteCallback() {
            @Override
            public void onSuccess(KinveyDeleteResponse kinveyDeleteResponse) {
                String imgName = laptopToRemove.getImageName();
                String imgPath = laptopToRemove.getImagePath();
                if (!imgPath.equals("")) {
                    File file = new File(imgPath, imgName);
                    boolean isDeleted = file.delete();
                }
                LoadDataService.this.mLaptopsDatabaseManager.deleteRecord(laptopToRemove.getId(), ConstantsHelper.LAPTOPS_TABLE_NAME);
                LoadDataService.this.mLaptopsDatabaseManager.deleteRecord(laptopToRemove.getId(), ConstantsHelper.ADMIN_REMOVED_LAPTOPS_TABLE_NAME);
                //Toast.makeText(LoadDataService.this, "File " + file.getName() + " isDeleted " + isDeleted, Toast.LENGTH_SHORT).show();
//                Toast.makeText(LoadDataService.this, "Laptop " + laptopToRemove.getModel() + " Successfully " +
//                        "deleted", Toast.LENGTH_SHORT).show();
                Intent endLoading = new Intent(ConstantsHelper.BROADCAST_END_LOADING);
                LoadDataService.this.sendBroadcast(endLoading);
            }

            @Override
            public void onFailure(Throwable throwable) {
                //   Toast.makeText(LoadDataService.this, "Fail to delete laptop", Toast.LENGTH_SHORT).show();
                Intent endLoading = new Intent(ConstantsHelper.BROADCAST_END_LOADING);
                LoadDataService.this.sendBroadcast(endLoading);
            }
        });
    }

    @Override
    public void onDestroy() {
        // Toast.makeText(this, "Service destroyed", Toast.LENGTH_SHORT).show();
        if (this.mKinveyClient != null) {
            this.mKinveyClient.user().logout().execute();
        }
        super.onDestroy();
    }

    public void uploadOrdersToKinvey(com.example.posted.models.User user) {
        ArrayList<Order> ordersToUpload = this.mLaptopsDatabaseManager.getAllOrders(ConstantsHelper.CURRENT_ORDERS_LAPTOPS_TABLE_NAME);
        if (ordersToUpload.size()> 0){
            Intent startLoading = new Intent(ConstantsHelper.BROADCAST_START_LOADING);
            this.sendBroadcast(startLoading);
            this.loginToKinvey();

            for (Order order : ordersToUpload) {
                order.setUser(user.getUsername());
                AsyncAppData<Order> laptopsInfo = this.mKinveyClient.appData(ConstantsHelper.KINVEY_ORDERS_TABLE_NAME, Order.class);
                laptopsInfo.save(order, new KinveyClientCallback<Order>() {
                    @Override
                    public void onSuccess(Order order) {
                        Toast.makeText(LoadDataService.this, LoadDataService.this.getResources().getString(R.string.order_successfully_sent), Toast.LENGTH_SHORT).show();
                        LoadDataService.this.mLaptopsDatabaseManager.deleteRecord(order.getId(), ConstantsHelper.CURRENT_ORDERS_LAPTOPS_TABLE_NAME);
                        Intent endLoading = new Intent(ConstantsHelper.BROADCAST_END_LOADING);
                        LoadDataService.this.sendBroadcast(endLoading);
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        // Toast.makeText(LoadDataService.this, "Fail to upload order", Toast.LENGTH_SHORT).show();
                        Intent endLoading = new Intent(ConstantsHelper.BROADCAST_END_LOADING);
                        LoadDataService.this.sendBroadcast(endLoading);
                    }
                });
            }
        }
    }

    public void downloadOrders() {
        Intent startLoading = new Intent(ConstantsHelper.BROADCAST_START_LOADING);
        this.sendBroadcast(startLoading);

        AsyncAppData<Order> ordersInfo = this.mKinveyClient.appData(ConstantsHelper.KINVEY_ORDERS_TABLE_NAME, Order.class);
        ordersInfo.get(new KinveyListCallback<Order>() {
            @Override
            public void onSuccess(Order[] orders) {
                //Toast.makeText(LoadDataService.this, "Successfully download orders", Toast.LENGTH_SHORT).show();
                for (Order order : orders) {
                    LoadDataService.this.mLaptopsDatabaseManager.insertOrderIntoTable(order);
                }
                Intent endLoading = new Intent(ConstantsHelper.BROADCAST_END_LOADING);
                LoadDataService.this.sendBroadcast(endLoading);
            }

            @Override
            public void onFailure(Throwable throwable) {
                //Toast.makeText(LoadDataService.this, "Fail to download orders", Toast.LENGTH_SHORT).show();
                Intent endLoading = new Intent(ConstantsHelper.BROADCAST_END_LOADING);
                LoadDataService.this.sendBroadcast(endLoading);
            }
        });
    }
}
