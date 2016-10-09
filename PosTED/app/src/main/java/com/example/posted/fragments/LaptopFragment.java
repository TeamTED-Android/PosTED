package com.example.posted.fragments;


import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.posted.R;
import com.example.posted.async.AsyncImageLoader;
import com.example.posted.constants.ConstantsHelper;
import com.example.posted.database.DatabaseManager;
import com.example.posted.database.LaptopsDatabaseManager;
import com.example.posted.interfaces.OnLaptopSelectedDataExchange;
import com.example.posted.interfaces.RemoveLaptopListener;
import com.example.posted.models.LaptopSqlite;
import com.example.posted.services.LoadDataService;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class LaptopFragment extends Fragment implements View.OnClickListener, AsyncImageLoader.Listener {

    private TextView mCurrentLptModel;
    private TextView mCurrentLptRam;
    private TextView mCurrentLptHdd;
    private TextView mCurrentLptProcessor;
    private TextView mCurrentLptVideoCard;
    private TextView mCurrentLptDisplay;
    private TextView mCurrentLptPrice;
    private TextView mCurrentLptCurrency;
    private ImageView mCurrentLptImage;
    private Button mLaptopFragmentButton;
    private OnLaptopSelectedDataExchange mBackToOverviewListener;
    private DatabaseManager mDatabaseManager;
    private LaptopsDatabaseManager mLaptopsDatabaseManager;
    private static RemoveLaptopListener mRemoveLaptopListener;

    private String mCurrentUser;
    private LoadDataService mLoadDataService;
    private Intent mServiceIntent;
    private boolean mIsBinded;
    private Context mContext;

    public LaptopFragment() {
        // Required empty public constructor
    }

    public static LaptopFragment newInstance(LaptopSqlite laptop, RemoveLaptopListener listener) {
        LaptopFragment fragment = new LaptopFragment();
        Bundle bundleLaptop = new Bundle();
        bundleLaptop.putParcelable(ConstantsHelper.LAPTOP_FRAGMENT_PARCELABLE_KEY, laptop);
        bundleLaptop.putBoolean("is_cart", true);
        fragment.setArguments(bundleLaptop);
        mRemoveLaptopListener = listener;
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mBackToOverviewListener = (OnLaptopSelectedDataExchange) context;
        this.mContext = context;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mBackToOverviewListener = (OnLaptopSelectedDataExchange) activity;
        this.mContext = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_laptop, container, false);
        LaptopSqlite currentLaptop = this.getArguments().getParcelable(ConstantsHelper.LAPTOP_FRAGMENT_PARCELABLE_KEY);
        if (currentLaptop == null) {
            return null;
        }
        String imagePath = currentLaptop.getImagePath();
        String imageName = currentLaptop.getImageName();
        if (imagePath == null || imageName == null) {
            return null;
        }
        AsyncImageLoader decoder = new AsyncImageLoader(this);
        decoder.execute(imagePath, imageName);

        this.mCurrentLptModel = (TextView) view.findViewById(R.id.current_lpt_model);
        this.mCurrentLptModel.setText(currentLaptop.getModel());

        this.mCurrentLptRam = (TextView) view.findViewById(R.id.current_lpt_ram);
        this.mCurrentLptRam.setText(currentLaptop.getCapacity_ram());

        this.mCurrentLptHdd = (TextView) view.findViewById(R.id.current_lpt_hdd);
        this.mCurrentLptHdd.setText(currentLaptop.getCapacity_hdd());

        this.mCurrentLptProcessor = (TextView) view.findViewById(R.id.current_lpt_processor);
        this.mCurrentLptProcessor.setText(currentLaptop.getProcessor_type());

        this.mCurrentLptVideoCard = (TextView) view.findViewById(R.id.current_lpt_video_card);
        this.mCurrentLptVideoCard.setText(currentLaptop.getVideo_card_type());

        this.mCurrentLptDisplay = (TextView) view.findViewById(R.id.current_lpt_display);
        this.mCurrentLptDisplay.setText(currentLaptop.getDisplay_size());

        this.mCurrentLptPrice = (TextView) view.findViewById(R.id.current_lpt_price);
        this.mCurrentLptPrice.setText(currentLaptop.getPrice());

        this.mCurrentLptCurrency = (TextView) view.findViewById(R.id.current_lpt_currency);
        this.mCurrentLptCurrency.setText(currentLaptop.getCurrency());

        this.mCurrentLptImage = (ImageView) view.findViewById(R.id.current_lpt_image);
        //TODO set image

        this.mLaptopFragmentButton = (Button) view.findViewById(R.id.laptop_fragment_button);
        this.mLaptopFragmentButton.setOnClickListener(this);

        if (this.getArguments().getBoolean("is_cart")) {
            this.mLaptopFragmentButton.setText("Remove from cart");
        } else {
            this.mCurrentUser = this.getArguments().getString(ConstantsHelper.FROM_WHERE_IS_INVOKED_KEY);
            if (this.mCurrentUser.equals("admin")) {
                this.mLaptopFragmentButton.setText("Remove from database");
                //check if service running and bind
                this.mServiceIntent = new Intent(this.mContext, LoadDataService.class);
                if (!this.isDataServiceRunning(LoadDataService.class)) {
                    this.mContext.startService(this.mServiceIntent);
                }
                this.mContext.bindService(this.mServiceIntent, this.connection, Context.BIND_AUTO_CREATE);
            }
            if (this.mCurrentUser.equals("user")) {
                this.mLaptopFragmentButton.setText("Add to cart");
            }
        }

        this.mDatabaseManager = new DatabaseManager(view.getContext());
        this.mLaptopsDatabaseManager = new LaptopsDatabaseManager(this.mDatabaseManager);

        return view;
    }

    @Override
    public void onClick(View v) {
        LaptopSqlite laptop = this.getArguments().getParcelable(ConstantsHelper.LAPTOP_FRAGMENT_PARCELABLE_KEY);
        if (this.getArguments().getBoolean("is_cart")) {
            this.mLaptopsDatabaseManager.deleteRecord(laptop, ConstantsHelper.CURRENT_ORDERS_LAPTOPS_TABLE_NAME);
            mRemoveLaptopListener.onRemoved();
            //  Toast.makeText(getContext(),"Laptop deleted from cart",Toast.LENGTH_SHORT).show();
        } else {
            if (this.mCurrentUser.equals("user")) {
                this.mLaptopsDatabaseManager.insertLaptopIntoTable(laptop, ConstantsHelper
                        .CURRENT_ORDERS_LAPTOPS_TABLE_NAME);
                Toast.makeText(this.getContext(), "Laptop added to cart", Toast.LENGTH_SHORT).show();
            }
            if (this.mCurrentUser.equals("admin")) {
                this.mLoadDataService.removeLaptopFromKinvey(laptop);
                this.mLoadDataService.transferDataFromKinvey();
                this.getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
            }
        }
    }

    @Override
    public void onDestroy() {
        if (this.mIsBinded) {
            this.mContext.unbindService(this.connection);
        }
        super.onDestroy();
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LoadDataService.LoadDataServiceBinder binder = (LoadDataService.LoadDataServiceBinder) service;
            LaptopFragment.this.mLoadDataService = binder.getService();
            LaptopFragment.this.mIsBinded = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LaptopFragment.this.mIsBinded = false;
        }
    };

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

    @Override
    public void onImageLoaded(Bitmap bitmap) {
        if (bitmap == null) {
            this.mCurrentLptImage.setImageResource(R.mipmap.no_image_black);
        } else {
            this.mCurrentLptImage.setImageBitmap(bitmap);
        }
    }
}
