package com.example.posted.fragments;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.posted.R;
import com.example.posted.interfaces.OnLaptopSelectedDataExchange;
import com.example.posted.models.LaptopSqlite;

/**
 * A simple {@link Fragment} subclass.
 */
public class LaptopFragment extends Fragment implements View.OnClickListener {

    private Button mBackToOverview;
    private TextView mCurrentLptModel;
    private TextView mCurrentLptRam;
    private TextView mCurrentLptHdd;
    private TextView mCurrentLptProcessor;
    private TextView mCurrentLptVideoCard;
    private TextView mCurrentLptDisplay;
    private TextView mCurrentLptPrice;
    private TextView mCurrentLptCurrency;
    private ImageView mCurrentLptImage;
    private OnLaptopSelectedDataExchange mBackToOverviewListener;


    public LaptopFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mBackToOverviewListener = (OnLaptopSelectedDataExchange) context;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mBackToOverviewListener = (OnLaptopSelectedDataExchange) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_laptop, container, false);
        LaptopSqlite currentLaptop = getArguments().getParcelable("current_laptop");

        this.mBackToOverview = (Button) view.findViewById(R.id.back_to_overview);
        this.mBackToOverview.setOnClickListener(this);

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
        return view;
    }

    @Override
    public void onClick(View view) {
        this.mBackToOverviewListener.onBackToOverviewButtonSelected();
    }
}
