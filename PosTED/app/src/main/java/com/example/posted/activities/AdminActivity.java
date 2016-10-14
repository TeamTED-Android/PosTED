package com.example.posted.activities;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.*;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

import com.example.posted.R;
import com.example.posted.adapters.SectionsPagerAdapter;
import com.example.posted.constants.ConstantsHelper;
import com.example.posted.database.DatabaseManager;
import com.example.posted.database.LaptopsDatabaseManager;
import com.example.posted.fragments.*;
import com.example.posted.interfaces.NetworkStateReceiverListener;
import com.example.posted.interfaces.OnLaptopSelectedDataExchange;
import com.example.posted.login.LoginManager;
import com.example.posted.models.LaptopSqlite;
import com.example.posted.receivers.NetworkStateReceiver;
import com.example.posted.services.LoadDataService;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnLaptopSelectedDataExchange,
        NetworkStateReceiverListener {

    public interface PermissionListener {
        void onPermissionsDenied();

        void onPermissionsGranted();
    }

    private PermissionListener mListener;
    private Intent mServiceIntent;
    private LoginManager mLoginManager;
    private NetworkStateReceiver mNetworkStateReceiver;
    private long back_pressed;
    private AdminActivity.BroadcastListener mBroadcastListener;
    private ViewPager mAdminContainerViewPager;
    private FrameLayout mAdminContainerFrameLayout;

    private DatabaseManager mDatabaseManager;
    private LaptopsDatabaseManager mLaptopsDatabaseManager;
    private LoadDataService mLoadDataService;
    private boolean mIsBindedSync;
    private boolean mIsBindedOrders;
    private Toolbar mToolbar;
    private MainFragment mMainFragment;
    private boolean mIsReceiverReady;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.admin_activity_main);
        this.mToolbar = (Toolbar) this.findViewById(R.id.toolbar);
        this.setSupportActionBar(this.mToolbar);

        FloatingActionButton fab = (FloatingActionButton) this.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, R.string.posted_email, Snackbar.LENGTH_LONG)
                        .setAction(AdminActivity.this.getResources().getString(R.string.send_us_email), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AdminActivity.this.sendMail();
                            }
                        }).show();
            }
        });
        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AdminActivity.this.sendMail();
                return true;
            }
        });
        DrawerLayout drawer = (DrawerLayout) this.findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, this.mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) this.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        this.mNetworkStateReceiver = new NetworkStateReceiver();
        this.mNetworkStateReceiver.addListener(this);
        this.registerReceiver(this.mNetworkStateReceiver, new IntentFilter(android.net.ConnectivityManager
                .CONNECTIVITY_ACTION));

        if (!this.checkForInternetConnection()) {
            this.attemptToTurnOnWiFi();
        }

        this.mLoginManager = new LoginManager(this);
        this.mMainFragment = new MainFragment();
        this.getSupportFragmentManager().beginTransaction().replace(R.id.adminContainer, this.mMainFragment).commit();

        TextView textView = (TextView) this.mToolbar.findViewById(R.id.admin_current_user);
        textView.setText(this.mLoginManager.getLoginUser().getUsername());
        textView.setGravity(Gravity.CENTER | Gravity.RIGHT);

        this.mBroadcastListener = new AdminActivity.BroadcastListener();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConstantsHelper.BROADCAST_START_LOADING);
        filter.addAction(ConstantsHelper.BROADCAST_END_LOADING);
        this.registerReceiver(this.mBroadcastListener, filter);

        this.mAdminContainerViewPager = (ViewPager) this.findViewById(R.id.admin_containerViewPager);
        this.mAdminContainerFrameLayout = (FrameLayout) this.findViewById(R.id.adminContainer);

        this.mDatabaseManager = new DatabaseManager(this);
        this.mLaptopsDatabaseManager = new LaptopsDatabaseManager(this.mDatabaseManager);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        this.getSupportFragmentManager().popBackStack();

        if (id == R.id.admin_nav_laptops) {
            this.mToolbar.setTitle(R.string.laptops);
            this.turnViewPagerVisibilityOff();
            OverviewFragment overviewFragment = new OverviewFragment();
            Bundle bundle = new Bundle();
            bundle.putCharSequence(ConstantsHelper.COLLECTION_CONSTANT, ConstantsHelper.OVERVIEW_LAPTOPS_COLLECTION);
            overviewFragment.setArguments(bundle);
            this.getSupportFragmentManager().beginTransaction().replace(R.id.adminContainer, overviewFragment)
                    .commit();

        } else if (id == R.id.admin_nav_phones) {
            this.mToolbar.setTitle(R.string.phones);
            this.turnViewPagerVisibilityOff();
            PhonesFragment phonesFragment = new PhonesFragment();
            this.getSupportFragmentManager().beginTransaction().replace(R.id.adminContainer, phonesFragment).commit();

        } else if (id == R.id.admin_nav_addProduct) {
            this.mToolbar.setTitle(R.string.add_product);
            this.turnViewPagerVisibilityOff();
            AddProductFragment fragment = new AddProductFragment();
            this.mListener = fragment;
            this.requestPermissions();
            this.getSupportFragmentManager().beginTransaction().replace(R.id.adminContainer, fragment).commit();

        } else if (id == R.id.admin_nav_signOut) {
            if (this.mIsReceiverReady) {
                this.turnViewPagerVisibilityOff();
                this.mLoginManager.logoutUser();
                this.getSupportFragmentManager().beginTransaction().remove(this.mMainFragment).commit();
                Intent intent = new Intent(this.getApplicationContext(), LoginActivity.class);
                this.finish();
                this.startActivity(intent);
            } else {
                Toast.makeText(this, this.getResources().getString(R.string.please_wait), Toast.LENGTH_SHORT).show();
            }

        } else if (id == R.id.admin_nav_home) {
            this.mToolbar.setTitle(R.string.app_name);
            this.turnViewPagerVisibilityOff();
            this.getSupportFragmentManager().beginTransaction().replace(R.id.adminContainer, this.mMainFragment).commit();

        } else if (id == R.id.admin_nav_profile) {
            this.mToolbar.setTitle(R.string.profile);
            this.turnViewPagerVisibilityOff();
            ProfileFragment profileFragment = new ProfileFragment();
            this.getSupportFragmentManager().beginTransaction().replace(R.id.adminContainer, profileFragment).commit();

        } else if (id == R.id.admin_nav_previewAddedProducts) {
            this.mToolbar.setTitle(R.string.preview_added_products);
            this.turnFrameLayoutVisibilityOff();
            SectionsPagerAdapter adapter = new SectionsPagerAdapter(this.getSupportFragmentManager(),
                    this,
                    ConstantsHelper.ADMIN_ADDED_LAPTOPS_TABLE_NAME,
                    ConstantsHelper.IS_ADD_LIST);
            ViewPager viewPager = (ViewPager) this.findViewById(R.id.admin_containerViewPager);
            viewPager.setAdapter(adapter);

        } else if (id == R.id.admin_nav_previewRemovedProducts) {
            this.mToolbar.setTitle(R.string.preview_removed_products);
            this.turnFrameLayoutVisibilityOff();
            SectionsPagerAdapter adapter = new SectionsPagerAdapter(this.getSupportFragmentManager(),
                    this,
                    ConstantsHelper.ADMIN_REMOVED_LAPTOPS_TABLE_NAME,
                    ConstantsHelper.IS_REMOVE_LIST);
            ViewPager viewPager = (ViewPager) this.findViewById(R.id.admin_containerViewPager);
            viewPager.setAdapter(adapter);

        } else if (id == R.id.admin_nav_sync) {
            this.turnViewPagerVisibilityOff();
            if (!this.checkForInternetConnection()) {
                this.attemptToTurnOnWiFi();
            }
            if (!this.isDataServiceRunning(LoadDataService.class)) {
                this.mServiceIntent = new Intent(this, LoadDataService.class);
                this.startService(this.mServiceIntent);
            }
            this.bindService(this.mServiceIntent, this.connectionSync, Context.BIND_AUTO_CREATE);

        } else if (id == R.id.admin_nav_orders) {
            this.turnViewPagerVisibilityOff();
            if (!this.checkForInternetConnection()) {
                this.attemptToTurnOnWiFi();
            }
            if (!this.isDataServiceRunning(LoadDataService.class)) {
                this.mServiceIntent = new Intent(this, LoadDataService.class);
                this.startService(this.mServiceIntent);
            }
            this.bindService(this.mServiceIntent, this.connectionOrders, Context.BIND_AUTO_CREATE);

            OverviewFragment overviewFragment = new OverviewFragment();
            Bundle bundle = new Bundle();
            bundle.putCharSequence(ConstantsHelper.COLLECTION_CONSTANT, ConstantsHelper.OVERVIEW_ORDERS_COLLECTION);
            overviewFragment.setArguments(bundle);
            this.getSupportFragmentManager().beginTransaction().replace(R.id.adminContainer, overviewFragment)
                    .commit();
        }

        DrawerLayout drawer = (DrawerLayout) this.findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onLaptopSelected(LaptopSqlite laptop) {
        Bundle bundleLaptop = new Bundle();
        bundleLaptop.putParcelable(ConstantsHelper.LAPTOP_FRAGMENT_PARCELABLE_KEY, laptop);
        bundleLaptop.putCharSequence(ConstantsHelper.FROM_WHERE_IS_INVOKED_KEY, ConstantsHelper.ADMIN);
        LaptopFragment laptopFragment = new LaptopFragment();
        laptopFragment.setArguments(bundleLaptop);
        this.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.adminContainer, laptopFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (this.back_pressed + ConstantsHelper.MILLISECONDS_FOR_DOUBLE_BACK > System.currentTimeMillis()) {
            this.getSupportFragmentManager().beginTransaction().remove(this.mMainFragment).commit();
            super.onBackPressed();
            return;
        }

        this.back_pressed = System.currentTimeMillis();
        Toast.makeText(this,this.getResources().getString(R.string.back_press),Toast.LENGTH_SHORT).show();
        DrawerLayout drawer = (DrawerLayout) this.findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (this.getSupportFragmentManager().getBackStackEntryCount() == 0) {
                this.mToolbar.setTitle(R.string.app_name);
                this.getSupportFragmentManager().beginTransaction().replace(R.id.adminContainer, this.mMainFragment).commit();
            } else {
                this.getSupportFragmentManager().popBackStack();
            }
        }
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
        this.registerReceiver(this.mBroadcastListener, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (this.mIsBindedSync) {
            this.unbindService(this.connectionSync);
        }
        if (this.mIsBindedOrders) {
            this.unbindService(this.connectionOrders);
        }
        if (this.isDataServiceRunning(LoadDataService.class)) {
            this.stopService(this.mServiceIntent);
        }
        this.unregisterReceiver(this.mNetworkStateReceiver);
        this.unregisterReceiver(this.mBroadcastListener);
    }

    private boolean checkForInternetConnection() {
        ConnectivityManager connectionManager = (ConnectivityManager) this.getSystemService(Context
                .CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectionManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private void attemptToTurnOnWiFi() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(this.getResources().getString(R.string.wifi_dialog_title));
        builder.setMessage(this.getResources().getString(R.string.wifi_dialog_message));
        builder.setPositiveButton(this.getResources().getString(R.string.wifi_dialog_positive_button), new
                DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        WifiManager wifiManager = (WifiManager) AdminActivity.this.getSystemService(Context
                                .WIFI_SERVICE);
                        wifiManager.setWifiEnabled(true);

                        Intent startLoading = new Intent(ConstantsHelper.BROADCAST_START_LOADING);
                        AdminActivity.this.sendBroadcast(startLoading);
                    }
                });
        builder.setNeutralButton(this.getResources().getString(R.string.wifi_dialog_neutral_button), new
                DialogInterface.OnClickListener() {
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
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{this.getResources().getString(R.string.posted_email)});
        i.putExtra(Intent.EXTRA_SUBJECT, this.getResources().getString(R.string.email_subject));
        i.putExtra(Intent.EXTRA_TEXT, this.getResources().getString(R.string.email_body));
        try {
            this.startActivity(Intent.createChooser(i, this.getResources().getString(R.string.send_us_email)));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(AdminActivity.this, this.getResources().getString(R.string.no_email_client), Toast.LENGTH_SHORT).show();
        }
    }

    private class BroadcastListener extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            SpinnerFragment spinnerFragment = new SpinnerFragment();
            if (intent.getAction().equals(ConstantsHelper.BROADCAST_START_LOADING)) {
                AdminActivity.this.getSupportFragmentManager()
                        .beginTransaction().replace(R.id.adminContainer, spinnerFragment).addToBackStack(null).commit();
                AdminActivity.this.mIsReceiverReady = false;
            } else if (intent.getAction().equals(ConstantsHelper.BROADCAST_END_LOADING)) {
                AdminActivity.this.getSupportFragmentManager().popBackStack();
                AdminActivity.this.mIsReceiverReady = true;
            }
        }
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_GRANTED) {
            return;
        }
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE}, ConstantsHelper.CAMERA_REQUESTS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[]
            grantResults) {
        if (grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            this.mListener.onPermissionsGranted();
        } else {
            this.mListener.onPermissionsDenied();
        }
    }

    private ServiceConnection connectionSync = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LoadDataService.LoadDataServiceBinder binder = (LoadDataService.LoadDataServiceBinder) service;
            AdminActivity.this.mLoadDataService = binder.getService();
            AdminActivity.this.mIsBindedSync = true;

            ArrayList<LaptopSqlite> laptopsForRemove = AdminActivity.this.mLaptopsDatabaseManager.getAllLaptops
                    (ConstantsHelper.ADMIN_REMOVED_LAPTOPS_TABLE_NAME);
            AdminActivity.this.mLoadDataService.removeLaptops(laptopsForRemove);
            ArrayList<LaptopSqlite> laptopsForAdd = AdminActivity.this.mLaptopsDatabaseManager.getAllLaptops
                    (ConstantsHelper.ADMIN_ADDED_LAPTOPS_TABLE_NAME);
            AdminActivity.this.mLoadDataService.uploadLaptops(laptopsForAdd);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            AdminActivity.this.mIsBindedSync = false;
        }
    };

    private ServiceConnection connectionOrders = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LoadDataService.LoadDataServiceBinder binder = (LoadDataService.LoadDataServiceBinder) service;
            AdminActivity.this.mLoadDataService = binder.getService();
            AdminActivity.this.mIsBindedOrders = true;
            AdminActivity.this.mLoadDataService.downloadOrders();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            AdminActivity.this.mIsBindedOrders = false;
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

    private void turnViewPagerVisibilityOff() {
        if (this.mAdminContainerViewPager.getVisibility() == View.VISIBLE) {
            this.mAdminContainerViewPager.setVisibility(View.INVISIBLE);
            this.mAdminContainerFrameLayout.setVisibility(View.VISIBLE);
        }
    }

    private void turnFrameLayoutVisibilityOff() {
        if (this.mAdminContainerFrameLayout.getVisibility() == View.VISIBLE) {
            this.mAdminContainerFrameLayout.setVisibility(View.INVISIBLE);
            this.mAdminContainerViewPager.setVisibility(View.VISIBLE);
        }
    }
}
