package com.example.posted.admin;

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
import android.widget.*;
import com.example.posted.R;
import com.example.posted.async.AsyncImageEncoder;
import com.example.posted.constants.ConstantsHelper;
import com.example.posted.database.DatabaseManager;
import com.example.posted.database.LaptopsDatabaseManager;
import com.example.posted.models.LaptopSqlite;
import com.example.posted.services.LoadDataService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class AddProductFragment extends Fragment implements View.OnClickListener, AsyncImageEncoder.Listener {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;

    private EditText model;
    private EditText price;
    private EditText hdd;
    private EditText ram;
    private EditText displaySize;
    private EditText processor;
    private EditText videoCard;
    private EditText currency;
    private ProgressBar progressBar;
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

    private Button getCameraButton(View root) {
        Button cameraButton = (Button) root.findViewById(R.id.camera_button);
        return cameraButton;
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LoadDataService.LoadDataServiceBinder binder = (LoadDataService.LoadDataServiceBinder) service;
            AddProductFragment.this.mLoadDataService = binder.getService();
            AddProductFragment.this.mIsBinded = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            AddProductFragment.this.mIsBinded = false;
        }
    };

    public AddProductFragment() {
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        this.databaseManager = new DatabaseManager(context);
        this.laptopsDatabaseManager = new LaptopsDatabaseManager(this.databaseManager);
    }

    @SuppressWarnings("deprecation")
    /*
        NOTE: Deprecated method is kept for backwards compatibility
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.context = activity;
        this.databaseManager = new DatabaseManager(this.context);
        this.laptopsDatabaseManager = new LaptopsDatabaseManager(this.databaseManager);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {
        View rootView = inflater.inflate(R.layout.admin_add_product_fragment, container, false);
        this.model = (EditText) rootView.findViewById(R.id.laptopModelEditText);
        this.price = (EditText) rootView.findViewById(R.id.priceEditText);
        this.hdd = (EditText) rootView.findViewById(R.id.hddEditText);
        this.ram = (EditText) rootView.findViewById(R.id.ramEditText);
        this.displaySize = (EditText) rootView.findViewById(R.id.displaySizeEditText);
        this.processor = (EditText) rootView.findViewById(R.id.processorEditText);
        this.videoCard = (EditText) rootView.findViewById(R.id.videoCardEditText);
        this.currency = (EditText) rootView.findViewById(R.id.currencyEditText);
        this.addImageButton = (Button) rootView.findViewById(R.id.browse_button);
        this.addProductButton = (Button) rootView.findViewById(R.id.addProductButton);
        this.cancelButton = (Button) rootView.findViewById(R.id.cancelButton);
        this.progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        Button cameraButton = this.getCameraButton(rootView);

        this.progressBar.setMax(100);
        this.progressBar.setVisibility(View.GONE);
        cameraButton.setOnClickListener(this);
        this.addProductButton.setOnClickListener(this);
        this.cancelButton.setOnClickListener(this);
        this.addImageButton.setOnClickListener(this);
//        this.imageAsString = "";

        this.laptopsDatabaseManager.createTempTable();
        this.mUploadButton = (Button) rootView.findViewById(R.id.uploadButton);
        this.mUploadButton.setOnClickListener(this);
        //check if service running and bind
        this.mServiceIntent = new Intent(this.context, LoadDataService.class);
        if (!this.isDataServiceRunning(LoadDataService.class)) {
            this.context.startService(this.mServiceIntent);
        }
        this.context.bindService(this.mServiceIntent, this.connection, Context.BIND_AUTO_CREATE);

        return rootView;
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
                this.laptopsDatabaseManager.insertRecord(currentLaptop, ConstantsHelper.TEMP_LAPTOPS_TABLE_NAME);
                Toast.makeText(this.context, "Laptop added to temp database", Toast.LENGTH_SHORT).show();
            }
        } else if (v.getId() == R.id.cancelButton) {

        } else if (v.getId() == R.id.browse_button) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            this.startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        } else if (v.getId() == R.id.uploadButton) {
            ArrayList<LaptopSqlite> tempLaptops = this.laptopsDatabaseManager.getAllLaptops(ConstantsHelper
                    .TEMP_LAPTOPS_TABLE_NAME);
            this.mLoadDataService.uploadLaptops(tempLaptops);
            this.databaseManager.deleteRecordsFromTable(ConstantsHelper.TEMP_LAPTOPS_TABLE_NAME);
        } else if (v.getId() == R.id.camera_button) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            this.startActivityForResult(intent, CAMERA_REQUEST);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK || data == null) {
            return;
        }
        if (requestCode == PICK_IMAGE_REQUEST) {
            this.onBrowseResult(data);
        } else if (requestCode == CAMERA_REQUEST) {
            this.onCameraResult(data);
        }
    }

    private void onCameraResult(Intent data) {
        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
        AsyncImageEncoder encoder = new AsyncImageEncoder(this);
        encoder.setProgressBar(this.progressBar);
        encoder.execute(bitmap);
    }

    private void onBrowseResult(Intent data) {
        Uri uri = data.getData();
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.context.getContentResolver(), uri);
            AsyncImageEncoder encoder = new AsyncImageEncoder(this);
            encoder.setProgressBar(this.progressBar);
            encoder.execute(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        if (this.mIsBinded) {
            this.context.unbindService(this.connection);
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

    @Override
    public void onImageEncoded(String base64str) {
        Toast.makeText(this.context, "Image loaded", Toast.LENGTH_SHORT).show();
        this.imageAsString = base64str;
    }
}
