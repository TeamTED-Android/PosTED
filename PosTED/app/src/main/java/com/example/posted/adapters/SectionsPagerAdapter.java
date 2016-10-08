package com.example.posted.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.example.posted.constants.ConstantsHelper;
import com.example.posted.database.DatabaseManager;
import com.example.posted.database.LaptopsDatabaseManager;
import com.example.posted.fragments.LaptopFragment;
import com.example.posted.interfaces.RemoveLaptopListener;
import com.example.posted.models.LaptopSqlite;

import java.util.ArrayList;
import java.util.List;


public class SectionsPagerAdapter extends FragmentStatePagerAdapter implements RemoveLaptopListener {

    private List<LaptopSqlite> mLaptops;
    private LaptopsDatabaseManager mLaptopsDatabaseManager;

    public SectionsPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        DatabaseManager databaseManager = new DatabaseManager(context);
        this.mLaptopsDatabaseManager = new LaptopsDatabaseManager(databaseManager);
        this.mLaptops = this.mLaptopsDatabaseManager.getAllLaptops(ConstantsHelper.CURRENT_ORDERS_LAPTOPS_TABLE_NAME);

    }

    @Override
    public Fragment getItem(int position) {
        return LaptopFragment.newInstance(this.mLaptops.get(position), this);
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
        this.mLaptops = this.mLaptopsDatabaseManager.getAllLaptops(ConstantsHelper.CURRENT_ORDERS_LAPTOPS_TABLE_NAME);
        this.notifyDataSetChanged();
    }
}


