package com.example.posted.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.example.posted.constants.ConstantsHelper;
import com.example.posted.database.DatabaseManager;
import com.example.posted.database.LaptopsDatabaseManager;
import com.example.posted.fragments.LaptopFragment;
import com.example.posted.fragments.NoItemsFragment;
import com.example.posted.interfaces.RemoveLaptopListener;
import com.example.posted.models.LaptopSqlite;

import java.util.ArrayList;
import java.util.List;


public class SectionsPagerAdapter extends FragmentStatePagerAdapter implements RemoveLaptopListener {

    private List<LaptopSqlite> mLaptops;
    private LaptopsDatabaseManager mLaptopsDatabaseManager;
    private String mTableName;
    private boolean isEmpty;
    private String mViewConstant;

    public SectionsPagerAdapter(FragmentManager fm, Context context, String tableName, String viewConstant) {
        super(fm);
        this.mTableName = tableName;
        DatabaseManager databaseManager = new DatabaseManager(context);
        this.mLaptopsDatabaseManager = new LaptopsDatabaseManager(databaseManager);
        this.loadData();
        this.mViewConstant = viewConstant;
    }

    @Override
    public Fragment getItem(int position) {
        if (this.isEmpty) {
            return NoItemsFragment.newInstance();
        }
        return LaptopFragment.newInstance(this.mLaptops.get(position), this,this.mViewConstant);
    }

    @Override
    public int getCount() {
        return this.mLaptops.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public void onRemoved() {
        this.mLaptops = new ArrayList<>();
        this.notifyDataSetChanged();
        this.loadData();
        this.notifyDataSetChanged();
    }

    private void loadData() {
        this.mLaptops = this.mLaptopsDatabaseManager.getAllLaptops(this.mTableName);
        if (this.mLaptops.size() == 0) {
            isEmpty = true;
            this.mLaptops.add(null);
        } else {
            this.isEmpty = false;
        }
    }


}


