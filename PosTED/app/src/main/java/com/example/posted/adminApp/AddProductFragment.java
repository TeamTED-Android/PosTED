package com.example.posted.adminApp;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.posted.async.AsyncImageEncoder;
import com.example.posted.services.LoadDataService;
import com.example.posted.R;
import com.example.posted.constants.ConstantsHelper;
import com.example.posted.database.DatabaseManager;
import com.example.posted.database.LaptopsDatabaseManager;
import com.example.posted.models.LaptopSqlite;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;


public class AddProductFragment extends Fragment implements View.OnClickListener, AsyncImageEncoder.Listener {
    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText model;
    private EditText price;
    private EditText hdd;
    private EditText ram;
    private EditText displaySize;
    private EditText processor;
    private EditText videoCard;
    private EditText currency;
    private Button addImageButton;
    private Button addProductButton;
    private Button cancelButton;
    private Context context;
    private String imageAsString;

    private DatabaseManager databaseManager;
    private LaptopsDatabaseManager laptopsDatabaseManager;
    private LoadDataService mLoadDataService;
    private Intent mServiceIntent;
    private boolean mIsBinded;
    private Button mUploadButton;

    public AddProductFragment() {
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        this.databaseManager = new DatabaseManager(context);
        this.laptopsDatabaseManager = new LaptopsDatabaseManager(this.databaseManager);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.context = activity;
        this.databaseManager = new DatabaseManager(context);
        this.laptopsDatabaseManager = new LaptopsDatabaseManager(this.databaseManager);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_add_product_fragment, container, false);
        this.model = (EditText) view.findViewById(R.id.laptopModelEditText);
        this.price = (EditText) view.findViewById(R.id.priceEditText);
        this.hdd = (EditText) view.findViewById(R.id.hddEditText);
        this.ram = (EditText) view.findViewById(R.id.ramEditText);
        this.displaySize = (EditText) view.findViewById(R.id.displaySizeEditText);
        this.processor = (EditText) view.findViewById(R.id.processorEditText);
        this.videoCard = (EditText) view.findViewById(R.id.videoCardEditText);
        this.currency = (EditText) view.findViewById(R.id.currencyEditText);

        this.addImageButton = (Button) view.findViewById(R.id.addImageButton);
        this.addProductButton = (Button) view.findViewById(R.id.addProductButton);
        this.cancelButton = (Button) view.findViewById(R.id.cancelButton);

        this.addProductButton.setOnClickListener(this);
        this.cancelButton.setOnClickListener(this);
        this.addImageButton.setOnClickListener(this);
        this.imageAsString = "";


        this.laptopsDatabaseManager.createTempTable();
        this.mUploadButton = (Button) view.findViewById(R.id.uploadButton);
        this.mUploadButton.setOnClickListener(this);
        //check if service running and bind
        this.mServiceIntent = new Intent(this.context, LoadDataService.class);
        if (!isDataServiceRunning(LoadDataService.class)){
            this.context.startService(this.mServiceIntent);
        }
        this.context.bindService(this.mServiceIntent, connection, Context.BIND_AUTO_CREATE);

        return view;
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.addProductButton) {
            if (this.checkInputInfo()) {

                LaptopSqlite currentLaptop = new LaptopSqlite(
                        this.model.getText().toString(),
                        this.ram.getText().toString(),
                        this.hdd.getText().toString(),
                        this.processor.getText().toString(),
                        this.videoCard.getText().toString(),
                        this.displaySize.getText().toString(),
                        this.currency.getText().toString(),
                        this.price.getText().toString(),
                        this.imageAsString);

                this.laptopsDatabaseManager.insertRecord(currentLaptop,ConstantsHelper.TEMP_LAPTOPS_TABLE_NAME);
                Toast.makeText(context,"Laptop added to temp database",Toast.LENGTH_SHORT).show();
            }
        } else if (v.getId() == R.id.cancelButton) {

        } else if (v.getId() == R.id.addImageButton) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            this.startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        }else if (v.getId() == R.id.uploadButton){
            ArrayList<LaptopSqlite> tempLaptops = this.laptopsDatabaseManager.getAllLaptops(ConstantsHelper.TEMP_LAPTOPS_TABLE_NAME);
            this.mLoadDataService.uploadLaptops(tempLaptops);
            this.databaseManager.deleteRecordsFromTable(ConstantsHelper.TEMP_LAPTOPS_TABLE_NAME);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.context.getContentResolver(), uri);
                AsyncImageEncoder encoder = new AsyncImageEncoder(this);
                encoder.execute(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        if (mIsBinded) {
            this.context.unbindService(connection);
        }
        super.onDestroy();
    }

    private boolean checkInputInfo() {

        if (this.model == null || this.model.getText().toString().equals("")) {
            this.model.setError("Laptop model cannot be empty");
            this.model.requestFocus();
            return false;
        } else if (this.hdd == null || this.hdd.getText().toString().equals("")) {
            this.hdd.setError("HDD cannot be empty");
            this.hdd.requestFocus();
            return false;
        } else if (this.displaySize == null || this.displaySize.getText().toString().equals("")) {
            this.displaySize.setError("Display size cannot be empty");
            this.displaySize.requestFocus();
            return false;
        } else if (this.processor == null || this.processor.getText().toString().equals("")) {
            this.processor.setError("Processor cannot be empty");
            this.processor.requestFocus();
            return false;
        } else if (this.videoCard == null || this.videoCard.getText().toString().equals("")) {
            this.videoCard.setError("Video card cannot be empty");
            this.videoCard.requestFocus();
            return false;
        } else if (this.currency == null || this.currency.getText().toString().equals("")) {
            this.currency.setError("Currency cannot be empty");
            this.currency.requestFocus();
            return false;
        } else if (this.price == null || this.price.getText().toString().equals("")) {
            this.price.setError("Price cannot be empty");
            this.price.requestFocus();
            return false;
        }

        return true;
    }

    private boolean isDataServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) this.context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> services = manager.getRunningServices(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo service : services) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

        ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LoadDataService.LoadDataServiceBinder binder = (LoadDataService.LoadDataServiceBinder) service;
            mLoadDataService = binder.getService();
            mIsBinded = true;

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIsBinded = false;
        }
    };

    @Override
    public void onImageEncoded(String base64str) {
        Toast.makeText(this.context, "Image loaded", Toast.LENGTH_SHORT).show();
        this.imageAsString = base64str;
    }
}
