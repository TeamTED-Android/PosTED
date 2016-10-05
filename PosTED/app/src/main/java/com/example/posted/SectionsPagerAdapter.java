package com.example.posted;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

import com.example.posted.constants.ConstantsHelper;
import com.example.posted.database.DatabaseManager;
import com.example.posted.database.LaptopsDatabaseManager;
import com.example.posted.fragments.LaptopFragment;
import com.example.posted.interfaces.RemoveLaptopListener;
import com.example.posted.models.LaptopSqlite;

import java.util.ArrayList;
import java.util.List;


public class SectionsPagerAdapter extends FragmentStatePagerAdapter implements RemoveLaptopListener{
    private List<LaptopSqlite> laptops;
    private DatabaseManager databaseManager;
    private LaptopsDatabaseManager laptopsDatabaseManager;

    public SectionsPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
       this.databaseManager = new DatabaseManager(context);
        this.laptopsDatabaseManager = new LaptopsDatabaseManager(this.databaseManager);
        this.laptops = this.laptopsDatabaseManager.getAllLaptops(ConstantsHelper.CURRENT_ORDERS_LAPTOPS_TABLE_NAME);

    }

    @Override
    public Fragment getItem(int position) {
        return LaptopFragment.newInstance(laptops.get(position),this);
    }

    @Override
    public int getCount() {
        return this.laptops.size();
    }


    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public void onRemoved() {
        this.laptops = new ArrayList<>();
        this.notifyDataSetChanged();
        this.laptops =   this.laptopsDatabaseManager.getAllLaptops(ConstantsHelper.CURRENT_ORDERS_LAPTOPS_TABLE_NAME);
        this.notifyDataSetChanged();
    }
}

