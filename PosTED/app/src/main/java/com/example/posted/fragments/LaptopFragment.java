package com.example.posted.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.posted.R;
import com.example.posted.constants.ConstantsHelper;
import com.example.posted.database.DatabaseManager;
import com.example.posted.database.LaptopsDatabaseManager;
import com.example.posted.interfaces.OnLaptopSelectedDataExchange;
import com.example.posted.interfaces.RemoveLaptopListener;
import com.example.posted.models.LaptopSqlite;
import com.example.posted.services.LoadDataService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * A simple {@link Fragment} subclass.
 */
public class LaptopFragment extends Fragment implements View.OnClickListener {

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

    public static LaptopFragment newInstance(LaptopSqlite laptop, RemoveLaptopListener listener, String viewConstant) {
        LaptopFragment fragment = new LaptopFragment();
        Bundle bundleLaptop = new Bundle();
        bundleLaptop.putParcelable(ConstantsHelper.LAPTOP_FRAGMENT_PARCELABLE_KEY, laptop);

        if (viewConstant.equals(ConstantsHelper.IS_CARD_LIST)) {
            bundleLaptop.putBoolean(ConstantsHelper.IS_CARD_LIST, true);
            bundleLaptop.putCharSequence(ConstantsHelper.FROM_WHERE_IS_INVOKED_KEY, ConstantsHelper.USER);
        } else if (viewConstant.equals(ConstantsHelper.IS_ADD_LIST)) {
            bundleLaptop.putBoolean(ConstantsHelper.IS_ADD_LIST, true);
            bundleLaptop.putCharSequence(ConstantsHelper.FROM_WHERE_IS_INVOKED_KEY, ConstantsHelper.ADMIN);
        } else if (viewConstant.equals(ConstantsHelper.IS_REMOVE_LIST)) {
            bundleLaptop.putBoolean(ConstantsHelper.IS_REMOVE_LIST, true);
            bundleLaptop.putCharSequence(ConstantsHelper.FROM_WHERE_IS_INVOKED_KEY, ConstantsHelper.ADMIN);
        }

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
        this.loadImageIntoImageView(imagePath, imageName);

        this.mLaptopFragmentButton = (Button) view.findViewById(R.id.laptop_fragment_button);
        this.mLaptopFragmentButton.setOnClickListener(this);

        this.mCurrentUser = this.getArguments().getString(ConstantsHelper.FROM_WHERE_IS_INVOKED_KEY);
        if (this.mCurrentUser.equals(ConstantsHelper.USER)) {
            if (this.getArguments().getBoolean(ConstantsHelper.IS_CARD_LIST)) {
                this.mLaptopFragmentButton.setText(this.getResources().getString(R.string.remove_from_cart));
            } else {
                this.mLaptopFragmentButton.setText(this.getResources().getString(R.string.add_to_cart));
            }
        }

        if (this.mCurrentUser.equals(ConstantsHelper.ADMIN)) {
            if (this.getArguments().getBoolean(ConstantsHelper.IS_ADD_LIST)) {
                this.mLaptopFragmentButton.setText(this.getResources().getString(R.string.remove_from_list));
            } else if (this.getArguments().getBoolean(ConstantsHelper.IS_REMOVE_LIST)) {
                this.mLaptopFragmentButton.setText(this.getResources().getString(R.string.remove_from_list));
            } else {
                this.mLaptopFragmentButton.setText(this.getResources().getString(R.string.remove_from_db));
            }
        }

        this.mDatabaseManager = new DatabaseManager(view.getContext());
        this.mLaptopsDatabaseManager = new LaptopsDatabaseManager(this.mDatabaseManager);

        return view;
    }

    private void loadImageIntoImageView(String imgPath, String imgName) {
        Bitmap bitmap = null;
        try {
            File file = new File(imgPath, imgName);
            bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (bitmap == null) {
            this.mCurrentLptImage.setImageResource(R.mipmap.no_image_black);
        } else {
            this.mCurrentLptImage.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onClick(View v) {
        LaptopSqlite laptop = this.getArguments().getParcelable(ConstantsHelper.LAPTOP_FRAGMENT_PARCELABLE_KEY);

        if (this.mCurrentUser.equals(ConstantsHelper.USER)) {
            if (this.getArguments().getBoolean(ConstantsHelper.IS_CARD_LIST)) {
                this.mLaptopsDatabaseManager.deleteRecord(laptop.getId(), ConstantsHelper.CURRENT_ORDERS_LAPTOPS_TABLE_NAME);
                mRemoveLaptopListener.onRemoved();
            } else {
                this.mLaptopsDatabaseManager.insertLaptopIntoTable(laptop, ConstantsHelper
                        .CURRENT_ORDERS_LAPTOPS_TABLE_NAME);
                Toast.makeText(this.getContext(), this.getResources().getString(R.string.laptop_added_to_cart), Toast.LENGTH_SHORT).show();
            }
        }

        if (this.mCurrentUser.equals(ConstantsHelper.ADMIN)) {
            if (this.getArguments().getBoolean(ConstantsHelper.IS_ADD_LIST)) {
                this.mLaptopsDatabaseManager.deleteRecord(laptop.getId(), ConstantsHelper.ADMIN_ADDED_LAPTOPS_TABLE_NAME);
                mRemoveLaptopListener.onRemoved();
            } else if (this.getArguments().getBoolean(ConstantsHelper.IS_REMOVE_LIST)) {
                this.mLaptopsDatabaseManager.deleteRecord(laptop.getId(), ConstantsHelper.ADMIN_REMOVED_LAPTOPS_TABLE_NAME);
                mRemoveLaptopListener.onRemoved();
            } else {
                this.mLaptopsDatabaseManager.insertLaptopIntoTable(laptop, ConstantsHelper.ADMIN_REMOVED_LAPTOPS_TABLE_NAME);
                Toast.makeText(this.mContext, this.getResources().getString(R.string.laptop_added_to_remove_list), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
