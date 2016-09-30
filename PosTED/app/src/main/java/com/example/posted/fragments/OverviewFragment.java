package com.example.posted.fragments;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.example.posted.LoadDataService;
import com.example.posted.R;
import com.example.posted.RecyclerViewAdapter;
import com.example.posted.models.LaptopSqlite;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class OverviewFragment extends Fragment implements RecyclerViewAdapter.RecyclerViewSelectedElement {

    public interface OnLaptopSelectedDataExchange{
        void onLaptopSelected(LaptopSqlite laptop);
    }

    private OverviewFragment.OnLaptopSelectedDataExchange mLaptopSelectedDataExchange;
    private Context ctx;
    private LoadDataService mLoadDataService;
    private Intent mServiceIntent;
    private boolean mIsBinded;
    private OverviewFragment mFragment;

    public OverviewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mLaptopSelectedDataExchange = (OnLaptopSelectedDataExchange) context;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mLaptopSelectedDataExchange = (OnLaptopSelectedDataExchange) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_overview, container, false);

        this.ctx = getContext();
        this.mFragment = this;
        this.mServiceIntent = new Intent(this.ctx, LoadDataService.class);
        getContext().bindService(this.mServiceIntent,connection,Context.BIND_AUTO_CREATE);


//        ArrayList<LaptopSqlite> result = getArguments().getParcelableArrayList("result");
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
//        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(layoutManager);
//
//        RecyclerViewAdapter adapter = new RecyclerViewAdapter(getContext(), result, this);
//        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onItemSelected(LaptopSqlite laptop) {
        this.mLaptopSelectedDataExchange.onLaptopSelected(laptop);
    }

    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LoadDataService.LoadDataServiceBinder binder = (LoadDataService.LoadDataServiceBinder) service;
            mLoadDataService = binder.getService();
            mIsBinded = true;
            ArrayList<LaptopSqlite> result = mLoadDataService.showResult();
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(layoutManager);

            RecyclerViewAdapter adapter = new RecyclerViewAdapter(ctx, result, mFragment);
            recyclerView.setAdapter(adapter);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIsBinded = false;
        }
    };
}
