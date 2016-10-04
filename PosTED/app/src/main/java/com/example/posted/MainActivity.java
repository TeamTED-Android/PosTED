package com.example.posted;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;


import com.example.posted.constants.ConstantsHelper;
import com.example.posted.fragments.LaptopFragment;
import com.example.posted.fragments.MainFragment;
import com.example.posted.fragments.OverviewFragment;
import com.example.posted.interfaces.OnLaptopSelectedDataExchange;
import com.example.posted.login.LoginActivity;
import com.example.posted.login.LoginManager;
import com.example.posted.models.LaptopSqlite;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnLaptopSelectedDataExchange {

    private Context ctx;
    private Intent mServiceIntent;
    private MainFragment mMainFragment;
    private LoginManager loginManager;
    private FrameLayout containerFrameLayoyt;
    private ViewPager conteinerViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        this.ctx = this;
        this.mServiceIntent = new Intent(this, LoadDataService.class);
        this.startService(this.mServiceIntent);
//
//        bindService(this.mServiceIntent, connection, Context.BIND_AUTO_CREATE);
        this.mMainFragment = new MainFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.container, this.mMainFragment).commit();

        this.loginManager = new LoginManager(this);

        this.containerFrameLayoyt = (FrameLayout) this.findViewById(R.id.container);
        this.conteinerViewPager = (ViewPager) this.findViewById(R.id.containerViewPager);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_laptops) {
            if (this.conteinerViewPager.getVisibility() == View.VISIBLE) {
                this.conteinerViewPager.setVisibility(View.INVISIBLE);
                this.containerFrameLayoyt.setVisibility(View.VISIBLE);
            }
            OverviewFragment overviewFragment = new OverviewFragment();
            this.getSupportFragmentManager().beginTransaction().replace(R.id.container, overviewFragment).addToBackStack(null).commit();
        } else if (id == R.id.nav_phones) {
            // show "coming soon'
        } else if (id == R.id.nav_sign_out) {
            if (this.conteinerViewPager.getVisibility() == View.VISIBLE) {
                this.conteinerViewPager.setVisibility(View.INVISIBLE);
                this.containerFrameLayoyt.setVisibility(View.VISIBLE);
            }
            loginManager.logoutUser();
            Intent intent = new Intent(this, LoginActivity.class);
            this.finish();
            this.startActivity(intent);
        } else if (id == R.id.home) {
            // show home
        } else if (id == R.id.nav_cart) {
            if (this.containerFrameLayoyt.getVisibility() == View.VISIBLE) {
                this.containerFrameLayoyt.setVisibility(View.INVISIBLE);
                this.conteinerViewPager.setVisibility(View.VISIBLE);
            }

            SectionsPagerAdapter adapter = new SectionsPagerAdapter(this.getSupportFragmentManager(), this);
            ViewPager viewPager = (ViewPager) this.findViewById(R.id.containerViewPager);
            viewPager.setAdapter(adapter);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onLaptopSelected(LaptopSqlite laptop) {
        Bundle bundleLaptop = new Bundle();
        bundleLaptop.putParcelable(ConstantsHelper.LAPTOP_FRAGMENT_PARCELABLE_KEY, laptop);
        LaptopFragment laptopFragment = new LaptopFragment();
        laptopFragment.setArguments(bundleLaptop);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, laptopFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    protected void onDestroy() {
        if (this.mServiceIntent != null) {
            stopService(this.mServiceIntent);
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                super.onBackPressed();
            } else {
                getSupportFragmentManager().popBackStack();
            }

        }
    }
}
