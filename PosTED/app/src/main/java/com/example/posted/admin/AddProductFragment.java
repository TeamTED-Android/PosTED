package com.example.posted.admin;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.*;
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
import android.widget.ProgressBar;
import android.widget.Toast;
import com.example.posted.R;
import com.example.posted.constants.ConstantsHelper;
import com.example.posted.database.DatabaseManager;
import com.example.posted.database.LaptopsDatabaseManager;
import com.example.posted.models.LaptopSqlite;
import com.example.posted.services.LoadDataService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class AddProductFragment extends Fragment implements View.OnClickListener {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;

    private EditText mModel;
    private EditText mPrice;
    private EditText mHdd;
    private EditText mRam;
    private EditText mDisplaySize;
    private EditText mProcessor;
    private EditText mVideoCard;
    private EditText mCurrency;
    private ProgressBar mProgressBar;
    private Button mAddImageButton;
    private Button mAddProductButton;
    private Button mCancelButton;
    private Context mContext;
    private String mImageAsString;
    private String mImagePath;
    private String mImageName;

    private DatabaseManager mDatabaseManager;
    private LaptopsDatabaseManager mLaptopsDatabaseManager;
    private LoadDataService mLoadDataService;
    private Intent mServiceIntent;
    private boolean mIsBinded;
    private Button mUploadButton;

    public AddProductFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
        this.mDatabaseManager = new DatabaseManager(context);
        this.mLaptopsDatabaseManager = new LaptopsDatabaseManager(this.mDatabaseManager);
    }

    @SuppressWarnings("deprecation")
    /*
        NOTE: Deprecated method is kept for backwards compatibility
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mContext = activity;
        this.mDatabaseManager = new DatabaseManager(this.mContext);
        this.mLaptopsDatabaseManager = new LaptopsDatabaseManager(this.mDatabaseManager);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {
        View rootView = inflater.inflate(R.layout.admin_add_product_fragment, container, false);
        this.mModel = (EditText) rootView.findViewById(R.id.laptopModelEditText);
        this.mPrice = (EditText) rootView.findViewById(R.id.priceEditText);
        this.mHdd = (EditText) rootView.findViewById(R.id.hddEditText);
        this.mRam = (EditText) rootView.findViewById(R.id.ramEditText);
        this.mDisplaySize = (EditText) rootView.findViewById(R.id.displaySizeEditText);
        this.mProcessor = (EditText) rootView.findViewById(R.id.processorEditText);
        this.mVideoCard = (EditText) rootView.findViewById(R.id.videoCardEditText);
        this.mCurrency = (EditText) rootView.findViewById(R.id.currencyEditText);
        this.mAddImageButton = (Button) rootView.findViewById(R.id.browse_button);
        this.mAddProductButton = (Button) rootView.findViewById(R.id.addProductButton);
        this.mCancelButton = (Button) rootView.findViewById(R.id.cancelButton);
        this.mProgressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        Button cameraButton = this.getCameraButton(rootView);

        this.mProgressBar.setMax(100);
        this.mProgressBar.setVisibility(View.GONE);
        cameraButton.setOnClickListener(this);
        this.mAddProductButton.setOnClickListener(this);
        this.mCancelButton.setOnClickListener(this);
        this.mAddImageButton.setOnClickListener(this);
        this.mImageAsString = "";

        this.mLaptopsDatabaseManager.createTempTable();
        this.mUploadButton = (Button) rootView.findViewById(R.id.uploadButton);
        this.mUploadButton.setOnClickListener(this);
        //check if service running and bind
        this.mServiceIntent = new Intent(this.mContext, LoadDataService.class);
        if (!this.isDataServiceRunning(LoadDataService.class)) {
            this.mContext.startService(this.mServiceIntent);
        }
        this.mContext.bindService(this.mServiceIntent, this.connection, Context.BIND_AUTO_CREATE);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.addProductButton) {
            this.onAddProductButtonClicked();
        } else if (v.getId() == R.id.cancelButton) {
            this.onCancelButtonClicked();
        } else if (v.getId() == R.id.browse_button) {
            this.onBrowseButtonClicked();
        } else if (v.getId() == R.id.uploadButton) {
            this.onUploadButtonClicked();
        } else if (v.getId() == R.id.camera_button) {
            this.onCameraButtonClicked();
        }
    }

    private void onCameraButtonClicked() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        this.startActivityForResult(intent, CAMERA_REQUEST);
    }

    private void onUploadButtonClicked() {
        ArrayList<LaptopSqlite> tempLaptops = this.mLaptopsDatabaseManager.getAllLaptops(ConstantsHelper
                .TEMP_LAPTOPS_TABLE_NAME);
        this.mLoadDataService.uploadLaptops(tempLaptops);
        this.mDatabaseManager.deleteRecordsFromTable(ConstantsHelper.TEMP_LAPTOPS_TABLE_NAME);
        this.mLoadDataService.transferDataFromKinvey();
    }

    private void onBrowseButtonClicked() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        this.startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void onCancelButtonClicked() {
    }

    private void onAddProductButtonClicked() {
        if (!this.checkInputInfo()) {
            return;
        }
        LaptopSqlite currentLaptop = new LaptopSqlite(
                this.mModel.getText().toString(),
                this.mRam.getText().toString(),
                this.mHdd.getText().toString(),
                this.mProcessor.getText().toString(),
                this.mVideoCard.getText().toString(),
                this.mDisplaySize.getText().toString(),
                this.mCurrency.getText().toString(),
                this.mPrice.getText().toString(),
                this.mImagePath,
                this.mImageName
        );
        this.mLaptopsDatabaseManager.insertLaptopIntoTable(currentLaptop, ConstantsHelper.TEMP_LAPTOPS_TABLE_NAME);
        Toast.makeText(this.mContext, "Laptop added to temp database", Toast.LENGTH_SHORT).show();
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

//    @Override
//    public void onImageEncoded(String base64str) {
//        Toast.makeText(this.mContext, "Image loaded", Toast.LENGTH_SHORT).show();
//        this.mImageAsString = base64str;
//    }

    private void onCameraResult(Intent data) {
        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
        int count = this.mLaptopsDatabaseManager.getRecordCount(ConstantsHelper.TEMP_LAPTOPS_TABLE_NAME);
        this.mImageName = "img" + count + ".png";
        this.mImagePath = this.saveToInternalStorage(bitmap);

//        AsyncImageEncoder encoder = new AsyncImageEncoder(this);
//        encoder.setProgressBar(this.mProgressBar);
//        encoder.execute(bitmap);
    }

    private String saveToInternalStorage(final Bitmap bitmapImage) {
        ContextWrapper cw = new ContextWrapper(this.mContext);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir(ConstantsHelper.IMAGE_DIRECTORY_PATH, Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory, this.mImageName);
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

    private void onBrowseResult(Intent data) {
        Uri uri = data.getData();
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.mContext.getContentResolver(), uri);
            int count = this.mLaptopsDatabaseManager.getRecordCount(ConstantsHelper.TEMP_LAPTOPS_TABLE_NAME);
            this.mImageName = "img" + count + ".png";
            this.mImagePath = this.saveToInternalStorage(bitmap);
//            AsyncImageEncoder encoder = new AsyncImageEncoder(this);
//            encoder.setProgressBar(this.mProgressBar);
//            encoder.execute(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        if (this.mIsBinded) {
            this.mContext.unbindService(this.connection);
        }
        super.onDestroy();
    }

    private boolean checkInputInfo() {

        if (this.mModel != null && this.mModel.getText().toString().equals("")) {
            this.mModel.setError("Laptop mModel cannot be empty");
            this.mModel.requestFocus();
            return false;
        } else if (this.mHdd != null && this.mHdd.getText().toString().equals("")) {
            this.mHdd.setError("HDD cannot be empty");
            this.mHdd.requestFocus();
            return false;
        } else if (this.mDisplaySize != null && this.mDisplaySize.getText().toString().equals("")) {
            this.mDisplaySize.setError("Display size cannot be empty");
            this.mDisplaySize.requestFocus();
            return false;
        } else if (this.mProcessor != null && this.mProcessor.getText().toString().equals("")) {
            this.mProcessor.setError("Processor cannot be empty");
            this.mProcessor.requestFocus();
            return false;
        } else if (this.mVideoCard != null && this.mVideoCard.getText().toString().equals("")) {
            this.mVideoCard.setError("Video card cannot be empty");
            this.mVideoCard.requestFocus();
            return false;
        } else if (this.mCurrency != null && this.mCurrency.getText().toString().equals("")) {
            this.mCurrency.setError("Currency cannot be empty");
            this.mCurrency.requestFocus();
            return false;
        } else if (this.mPrice != null && this.mPrice.getText().toString().equals("")) {
            this.mPrice.setError("Price cannot be empty");
            this.mPrice.requestFocus();
            return false;
        }
        return true;
    }

    private boolean isDataServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) this.mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> services = manager.getRunningServices(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo service : services) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
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

    private Button getCameraButton(View root) {
        Button cameraButton = (Button) root.findViewById(R.id.camera_button);
        return cameraButton;
    }
}
