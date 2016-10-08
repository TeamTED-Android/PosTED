package com.example.posted.fragments;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.posted.R;
import com.example.posted.adapters.RecyclerViewAdapter;
import com.example.posted.constants.ConstantsHelper;
import com.example.posted.database.DatabaseManager;
import com.example.posted.database.LaptopsDatabaseManager;
import com.example.posted.interfaces.OnLaptopSelectedDataExchange;
import com.example.posted.models.LaptopSqlite;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class OverviewFragment extends Fragment implements RecyclerViewAdapter.RecyclerViewSelectedElement {

    private OnLaptopSelectedDataExchange mLaptopSelectedDataExchange;

    private DatabaseManager mDatabaseManager;
    private LaptopsDatabaseManager mLaptopsDatabaseManager;
    private Context mContext;

    public OverviewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
        this.mLaptopSelectedDataExchange = (OnLaptopSelectedDataExchange) this.mContext;
        this.mDatabaseManager = new DatabaseManager(this.mContext);
        this.mLaptopsDatabaseManager = new LaptopsDatabaseManager(this.mDatabaseManager);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mContext = activity;
        this.mLaptopSelectedDataExchange = (OnLaptopSelectedDataExchange) this.mContext;
        this.mDatabaseManager = new DatabaseManager(this.mContext);
        this.mLaptopsDatabaseManager = new LaptopsDatabaseManager(this.mDatabaseManager);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_overview, container, false);

        ArrayList<LaptopSqlite> result = this.mLaptopsDatabaseManager.getAllLaptops(ConstantsHelper.LAPTOPS_TABLE_NAME);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getContext(), LinearLayoutManager
                .VERTICAL, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);

        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this.getContext(), result, this);
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onItemSelected(LaptopSqlite laptop) {
        this.mLaptopSelectedDataExchange.onLaptopSelected(laptop);
    }
}
