package com.example.posted.fragments;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.posted.LoadDataService;
import com.example.posted.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements View.OnClickListener {

    public interface ButtonsExchangeData{
        void loginButtonClicked();
        void getInfoButtonClicked();
        void showResultButtonClicked();
    }

    private TextView mInfoView;
    private Button mLogin;
    private Button mGetInfo;
    private Button mShowResult;
    private MainFragment.BroadcastListener mBroadcastListener;
    private ButtonsExchangeData mButtonExchangeData;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mButtonExchangeData = (ButtonsExchangeData) context;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mButtonExchangeData = (ButtonsExchangeData) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        this.mInfoView = (TextView) view.findViewById(R.id.info);
        this.mLogin = (Button) view.findViewById(R.id.log);
        this.mLogin.setOnClickListener(this);
        this.mGetInfo = (Button) view.findViewById(R.id.get_info);
        this.mGetInfo.setOnClickListener(this);
        this.mShowResult = (Button) view.findViewById(R.id.show_result);
        this.mShowResult.setOnClickListener(this);
        this.mInfoView.setText("My data is stored in Kinvey. Please select first \"Login\" button to login in Kinvey. Then select \"Get info\" button to transfer the data from Kinvey to SQLite. And finally select \"Show result\" button to see the result.");

        mBroadcastListener = new MainFragment.BroadcastListener();
        IntentFilter filter = new IntentFilter();
        filter.addAction(LoadDataService.BROADCAST_ACTION_LOGIN);
        filter.addAction(LoadDataService.BROADCAST_ACTION_GET_INFO);
        getContext().registerReceiver(mBroadcastListener, filter);

        return view;
    }

    private class BroadcastListener extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(LoadDataService.BROADCAST_ACTION_LOGIN)) {
                mInfoView.setText(intent.getStringExtra("info_login"));
            }else if (intent.getAction().equals(LoadDataService.BROADCAST_ACTION_GET_INFO)){
                mInfoView.setText(intent.getStringExtra("info_get_info"));
            }
        }
    }

    @Override
    public void onDestroy() {
        getContext().unregisterReceiver(this.mBroadcastListener);
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.log:
                this.mButtonExchangeData.loginButtonClicked();
                break;
            case R.id.get_info:
                this.mButtonExchangeData.getInfoButtonClicked();
                break;
            case R.id.show_result:
                this.mButtonExchangeData.showResultButtonClicked();
                break;
        }

    }
}
