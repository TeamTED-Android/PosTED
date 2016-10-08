package com.example.posted;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
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
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.posted.adapters.SectionsPagerAdapter;
import com.example.posted.constants.ConstantsHelper;
import com.example.posted.fragments.LaptopFragment;
import com.example.posted.fragments.MainFragment;
import com.example.posted.fragments.OverviewFragment;
import com.example.posted.fragments.PhonesFragment;
import com.example.posted.fragments.ProfileFragment;
import com.example.posted.fragments.SpinnerFragment;
import com.example.posted.interfaces.NetworkStateReceiverListener;
import com.example.posted.interfaces.OnLaptopSelectedDataExchange;
import com.example.posted.login.LoginActivity;
import com.example.posted.login.LoginManager;
import com.example.posted.models.LaptopSqlite;
import com.example.posted.receivers.NetworkStateReceiver;
import com.example.posted.services.LoadDataService;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnLaptopSelectedDataExchange,
        NetworkStateReceiverListener {

    private Context mCtx;
    private Intent mServiceIntent;
    private MainFragment mMainFragment;
    private LoginManager mLoginManager;
    private FrameLayout mContainerFrameLayoyt;
    private ViewPager mConteinerViewPager;
    private NetworkStateReceiver mNetworkStateReceiver;
    private DrawerLayout mDrawer;
    private long back_pressed;

    private MainActivity.BroadcastListener mBroadcastListener; ///////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);

        // toolbar.setSubtitle(this.mLoginManager.getLoginUser().getUsername());

        FloatingActionButton fab = (FloatingActionButton) this.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, R.string.posted_email, Snackbar.LENGTH_LONG)
                        .setAction("send us email", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                MainActivity.this.sendMail();
                            }
                        }).show();
            }
        });

        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                MainActivity.this.sendMail();
                return true;
            }


        });

        this.mDrawer = (DrawerLayout) this.findViewById(R.id.drawer_layout);
        //drawer.openDrawer(Gravity.LEFT);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, this.mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        this.mDrawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) this.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        this.mNetworkStateReceiver = new NetworkStateReceiver();
        this.mNetworkStateReceiver.addListener(this);
        this.registerReceiver(this.mNetworkStateReceiver, new IntentFilter(android.net.ConnectivityManager
                .CONNECTIVITY_ACTION));

        this.mCtx = this;

        if (!this.checkForInternetConnection()) {
            this.attemptToTurnOnWiFi();
        }

//        bindService(this.mServiceIntent, connection, Context.BIND_AUTO_CREATE);
        this.mMainFragment = new MainFragment();
        this.getSupportFragmentManager().beginTransaction().replace(R.id.container, this.mMainFragment).commit();

        this.mLoginManager = new LoginManager(this);

        TextView textView = (TextView) toolbar.findViewById(R.id.current_user);
        textView.setText(this.mLoginManager.getLoginUser().getUsername());
        textView.setGravity(Gravity.CENTER | Gravity.RIGHT);

        this.mContainerFrameLayoyt = (FrameLayout) this.findViewById(R.id.container);
        this.mConteinerViewPager = (ViewPager) this.findViewById(R.id.containerViewPager);

        //////////////////////////////////////////////////////////////////
        mBroadcastListener = new MainActivity.BroadcastListener();
        IntentFilter filter = new IntentFilter();
        filter.addAction(LoadDataService.BROADCAST_START_LOADING);
        filter.addAction(LoadDataService.BROADCAST_END_LOADING);
        this.registerReceiver(mBroadcastListener, filter);


    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        this.getSupportFragmentManager().popBackStack();

        if (id == R.id.nav_laptops) {
            if (this.mConteinerViewPager.getVisibility() == View.VISIBLE) {
                this.mConteinerViewPager.setVisibility(View.INVISIBLE);
                this.mContainerFrameLayoyt.setVisibility(View.VISIBLE);
            }
            OverviewFragment overviewFragment = new OverviewFragment();
            this.getSupportFragmentManager().beginTransaction().replace(R.id.container, overviewFragment)
                    .commit();
        } else if (id == R.id.nav_phones) {
            if (this.mConteinerViewPager.getVisibility() == View.VISIBLE) {
                this.mConteinerViewPager.setVisibility(View.INVISIBLE);
                this.mContainerFrameLayoyt.setVisibility(View.VISIBLE);
            }
            PhonesFragment phonesFragment = new PhonesFragment();
            this.getSupportFragmentManager().beginTransaction().replace(R.id.container, phonesFragment).commit();
        } else if (id == R.id.nav_sign_out) {
            if (this.mConteinerViewPager.getVisibility() == View.VISIBLE) {
                this.mConteinerViewPager.setVisibility(View.INVISIBLE);
                this.mContainerFrameLayoyt.setVisibility(View.VISIBLE);
            }
            this.mLoginManager.logoutUser();
            Intent intent = new Intent(this, LoginActivity.class);
            this.finish();
            this.startActivity(intent);
        } else if (id == R.id.nav_home) {
            if (this.mConteinerViewPager.getVisibility() == View.VISIBLE) {
                this.mConteinerViewPager.setVisibility(View.INVISIBLE);
                this.mContainerFrameLayoyt.setVisibility(View.VISIBLE);
            }
            MainFragment mainFragment = new MainFragment();
            this.getSupportFragmentManager().beginTransaction().replace(R.id.container, mainFragment).commit();
        } else if (id == R.id.nav_cart) {
            if (this.mContainerFrameLayoyt.getVisibility() == View.VISIBLE) {
                this.mContainerFrameLayoyt.setVisibility(View.INVISIBLE);
                this.mConteinerViewPager.setVisibility(View.VISIBLE);
            }

            SectionsPagerAdapter adapter = new SectionsPagerAdapter(this.getSupportFragmentManager(), this);
            ViewPager viewPager = (ViewPager) this.findViewById(R.id.containerViewPager);
            viewPager.setAdapter(adapter);
        } else if(id == R.id.nav_profile){
            if (this.mConteinerViewPager.getVisibility() == View.VISIBLE) {
                this.mConteinerViewPager.setVisibility(View.INVISIBLE);
                this.mContainerFrameLayoyt.setVisibility(View.VISIBLE);
            }
            ProfileFragment profileFragment = new ProfileFragment();
            this.getSupportFragmentManager().beginTransaction().replace(R.id.container,profileFragment).commit();
        }

        DrawerLayout drawer = (DrawerLayout) this.findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onLaptopSelected(LaptopSqlite laptop) {
        Bundle bundleLaptop = new Bundle();
        bundleLaptop.putParcelable(ConstantsHelper.LAPTOP_FRAGMENT_PARCELABLE_KEY, laptop);
        bundleLaptop.putCharSequence(ConstantsHelper.FROM_WHERE_IS_INVOKED_KEY, "user");
        LaptopFragment laptopFragment = new LaptopFragment();
        laptopFragment.setArguments(bundleLaptop);
        this.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, laptopFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void networkAvailable() {
        this.mServiceIntent = new Intent(this, LoadDataService.class);
        this.startService(this.mServiceIntent);
        ////////////////////////////////////////////////////////////////////
        Intent endLoading = new Intent(LoadDataService.BROADCAST_END_LOADING);
        sendBroadcast(endLoading);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.registerReceiver(this.mNetworkStateReceiver, new IntentFilter(android.net.ConnectivityManager
                .CONNECTIVITY_ACTION));
        ////////////////////////////////////////////////////////
        IntentFilter filter = new IntentFilter();
        filter.addAction(LoadDataService.BROADCAST_START_LOADING);
        filter.addAction(LoadDataService.BROADCAST_END_LOADING);
        this.registerReceiver(mBroadcastListener, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (this.mServiceIntent != null) {
            this.stopService(this.mServiceIntent);
        }
        this.unregisterReceiver(this.mNetworkStateReceiver);
        ////////////////////////////////////////////////
        this.unregisterReceiver(this.mBroadcastListener);
    }


    @Override
    public void onBackPressed() {
        if (this.back_pressed + 1500 > System.currentTimeMillis()) {
            super.onBackPressed();
        }
        this.back_pressed = System.currentTimeMillis();

        DrawerLayout drawer = (DrawerLayout) this.findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (this.getSupportFragmentManager().getBackStackEntryCount() == 0) {
                MainFragment mainFragment = new MainFragment();
                this.getSupportFragmentManager().beginTransaction().replace(R.id.container, mainFragment).commit();
            } else {
                this.getSupportFragmentManager().popBackStack();
            }
        }
    }

    private boolean checkForInternetConnection() {
        ConnectivityManager connectionManager = (ConnectivityManager) this.getSystemService(Context
                .CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectionManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private void attemptToTurnOnWiFi() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No internet connection!");
        builder.setMessage("This App needs Internet connection to update the database!");
        builder.setPositiveButton("Turn on WiFi", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                WifiManager wifiManager = (WifiManager) MainActivity.this.getSystemService(Context.WIFI_SERVICE);
                wifiManager.setWifiEnabled(true);
                ////////////////////////////////////////////////////////////////////////
                Intent startLoading = new Intent(LoadDataService.BROADCAST_START_LOADING);
                sendBroadcast(startLoading);
                //startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
            }
        });
        builder.setNeutralButton("Work Offline", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void sendMail() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{"office.posted@gmail.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
        i.putExtra(Intent.EXTRA_TEXT, "body of email");
        try {
            this.startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MainActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    //////////////////////////////////////////////////////////////////
    private class BroadcastListener extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            SpinnerFragment spinnerFragment = new SpinnerFragment();
            if (intent.getAction().equals(LoadDataService.BROADCAST_START_LOADING)) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, spinnerFragment).addToBackStack(null).commit();
            } else if (intent.getAction().equals(LoadDataService.BROADCAST_END_LOADING)) {
                getSupportFragmentManager().popBackStack();
                //mDrawer.openDrawer(Gravity.LEFT);
            }
        }
    }
}
