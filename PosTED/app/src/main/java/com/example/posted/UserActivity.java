package com.example.posted;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.*;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
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
import com.example.posted.admin.AdminActivity;
import com.example.posted.constants.ConstantsHelper;
import com.example.posted.fragments.*;
import com.example.posted.interfaces.NetworkStateReceiverListener;
import com.example.posted.interfaces.OnLaptopSelectedDataExchange;
import com.example.posted.login.LoginActivity;
import com.example.posted.login.LoginManager;
import com.example.posted.models.LaptopSqlite;
import com.example.posted.models.Order;
import com.example.posted.receivers.NetworkStateReceiver;
import com.example.posted.services.LoadDataService;

import java.util.ArrayList;
import java.util.List;


public class UserActivity extends AppCompatActivity
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
    private UserActivity.BroadcastListener mBroadcastListener;
    private Toolbar mToolbar;
    private boolean mIsBinded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);
        this.mToolbar = (Toolbar) this.findViewById(R.id.toolbar);
        this.setSupportActionBar( this.mToolbar);

        // toolbar.setSubtitle(this.mLoginManager.getLoginUser().getUsername());

        FloatingActionButton fab = (FloatingActionButton) this.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, R.string.posted_email, Snackbar.LENGTH_LONG)
                        .setAction("send us email", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                UserActivity.this.sendMail();
                            }
                        }).show();
            }
        });

        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                UserActivity.this.sendMail();
                return true;
            }


        });

        this.mDrawer = (DrawerLayout) this.findViewById(R.id.drawer_layout);
        //drawer.openDrawer(Gravity.LEFT);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, this.mDrawer,  this.mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
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

        TextView textView = (TextView)  this.mToolbar.findViewById(R.id.current_user);
        textView.setText(this.mLoginManager.getLoginUser().getUsername());
        textView.setGravity(Gravity.CENTER | Gravity.RIGHT);

        this.mContainerFrameLayoyt = (FrameLayout) this.findViewById(R.id.container);
        this.mConteinerViewPager = (ViewPager) this.findViewById(R.id.containerViewPager);

        mBroadcastListener = new UserActivity.BroadcastListener();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConstantsHelper.BROADCAST_START_LOADING);
        filter.addAction(ConstantsHelper.BROADCAST_END_LOADING);
        this.registerReceiver(mBroadcastListener, filter);


    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        this.getSupportFragmentManager().popBackStack();

        if (id == R.id.nav_laptops) {
            this.mToolbar.setTitle(R.string.laptops);
            this.turnViewPagerVisibilityOff();
            OverviewFragment overviewFragment = new OverviewFragment();
            Bundle bundle = new Bundle();
            bundle.putCharSequence(ConstantsHelper.COLLECTION_CONSTANT, ConstantsHelper.OVERVIEW_LAPTOPS_COLLECTION);
            overviewFragment.setArguments(bundle);
            this.getSupportFragmentManager().beginTransaction().replace(R.id.container, overviewFragment)
                    .commit();
        } else if (id == R.id.nav_phones) {
            this.mToolbar.setTitle(R.string.phones);
            this.turnViewPagerVisibilityOff();
            PhonesFragment phonesFragment = new PhonesFragment();
            this.getSupportFragmentManager().beginTransaction().replace(R.id.container, phonesFragment).commit();
        } else if (id == R.id.nav_sign_out) {
            this.turnViewPagerVisibilityOff();
            this.mLoginManager.logoutUser();
            Intent intent = new Intent(this, LoginActivity.class);
            this.finish();
            this.startActivity(intent);
        } else if (id == R.id.nav_home) {
            this.mToolbar.setTitle(R.string.app_name);
            this.turnViewPagerVisibilityOff();
            MainFragment mainFragment = new MainFragment();
            this.getSupportFragmentManager().beginTransaction().replace(R.id.container, mainFragment).commit();
        } else if (id == R.id.nav_cart) {
            this.mToolbar.setTitle(R.string.cart);
            this.turnFrameLayoutVisibilityOff();
            SectionsPagerAdapter adapter = new SectionsPagerAdapter(this.getSupportFragmentManager(),
                                                                    this,
                                                                    ConstantsHelper.CURRENT_ORDERS_LAPTOPS_TABLE_NAME,
                                                                    ConstantsHelper.IS_CARD_LIST);
            ViewPager viewPager = (ViewPager) this.findViewById(R.id.containerViewPager);
            viewPager.setAdapter(adapter);
        } else if (id == R.id.nav_profile) {
            this.mToolbar.setTitle(R.string.profile);
            this.turnViewPagerVisibilityOff();
            ProfileFragment profileFragment = new ProfileFragment();
            this.getSupportFragmentManager().beginTransaction().replace(R.id.container, profileFragment).commit();
        } else if (id == R.id.nav_checkout){
            if (!this.isDataServiceRunning(LoadDataService.class)) {
                this.mServiceIntent = new Intent(this, LoadDataService.class);
                this.startService(this.mServiceIntent);
            }
            this.bindService(this.mServiceIntent, this.connection, Context.BIND_AUTO_CREATE);

        }

        DrawerLayout drawer = (DrawerLayout) this.findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onLaptopSelected(LaptopSqlite laptop) {
        Bundle bundleLaptop = new Bundle();
        bundleLaptop.putParcelable(ConstantsHelper.LAPTOP_FRAGMENT_PARCELABLE_KEY, laptop);
        bundleLaptop.putCharSequence(ConstantsHelper.FROM_WHERE_IS_INVOKED_KEY, ConstantsHelper.USER);
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
        Intent endLoading = new Intent(ConstantsHelper.BROADCAST_END_LOADING);
        this.sendBroadcast(endLoading);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.registerReceiver(this.mNetworkStateReceiver, new IntentFilter(android.net.ConnectivityManager
                .CONNECTIVITY_ACTION));
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConstantsHelper.BROADCAST_START_LOADING);
        filter.addAction(ConstantsHelper.BROADCAST_END_LOADING);
        this.registerReceiver(mBroadcastListener, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (this.mIsBinded){
            this.unbindService(connection);
        }
        if (this.mServiceIntent != null) {
            this.stopService(this.mServiceIntent);
        }
        this.unregisterReceiver(this.mNetworkStateReceiver);
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
                this.mToolbar.setTitle(R.string.app_name);
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
        builder.setTitle(getResources().getString(R.string.wifi_dialog_title));
        builder.setMessage(getResources().getString(R.string.wifi_dialog_message));
        builder.setPositiveButton(getResources().getString(R.string.wifi_dialog_positive_button), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                WifiManager wifiManager = (WifiManager) UserActivity.this.getSystemService(Context.WIFI_SERVICE);
                wifiManager.setWifiEnabled(true);
                Intent startLoading = new Intent(ConstantsHelper.BROADCAST_START_LOADING);
                UserActivity.this.sendBroadcast(startLoading);
                //startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
            }
        });
        builder.setNeutralButton(getResources().getString(R.string.wifi_dialog_neutral_button), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void sendMail() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType(ConstantsHelper.MESSAGE_TYPE);
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{getResources().getString(R.string.posted_email)});
        i.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.email_subject));
        i.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.email_body));
        try {
            this.startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(UserActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    private class BroadcastListener extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            turnViewPagerVisibilityOff();
            SpinnerFragment spinnerFragment = new SpinnerFragment();
            if (intent.getAction().equals(ConstantsHelper.BROADCAST_START_LOADING)) {
                UserActivity.this.getSupportFragmentManager()
                        .beginTransaction().replace(R.id.container, spinnerFragment).addToBackStack(null).commit();
            } else if (intent.getAction().equals(ConstantsHelper.BROADCAST_END_LOADING)) {
                UserActivity.this.getSupportFragmentManager().popBackStack();
                //mDrawer.openDrawer(Gravity.LEFT);
            }
        }
    }

    private void turnFrameLayoutVisibilityOff() {
        if (this.mContainerFrameLayoyt.getVisibility() == View.VISIBLE) {
            this.mContainerFrameLayoyt.setVisibility(View.INVISIBLE);
            this.mConteinerViewPager.setVisibility(View.VISIBLE);
        }
    }

    private void turnViewPagerVisibilityOff() {
        if (this.mConteinerViewPager.getVisibility() == View.VISIBLE) {
            this.mConteinerViewPager.setVisibility(View.INVISIBLE);
            this.mContainerFrameLayoyt.setVisibility(View.VISIBLE);
        }
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LoadDataService.LoadDataServiceBinder binder = (LoadDataService.LoadDataServiceBinder) service;
            LoadDataService mLoadDataService = binder.getService();
            UserActivity.this.mIsBinded = true;
            mLoadDataService.uploadOrdersToKinvey(UserActivity.this.mLoginManager.getLoginUser());

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            UserActivity.this.mIsBinded = false;
        }
    };

    private boolean isDataServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> services = manager.getRunningServices(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo service : services) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
