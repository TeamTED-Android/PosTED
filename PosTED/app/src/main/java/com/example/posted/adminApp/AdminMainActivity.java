package com.example.posted.adminApp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.posted.LoadDataService;
import com.example.posted.R;
import com.example.posted.fragments.LaptopFragment;
import com.example.posted.fragments.MainFragment;
import com.example.posted.fragments.OverviewFragment;
import com.example.posted.interfaces.OnLaptopSelectedDataExchange;
import com.example.posted.login.LoginActivity;
import com.example.posted.login.LoginManager;
import com.example.posted.models.LaptopSqlite;

public class AdminMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnLaptopSelectedDataExchange {

    private Context ctx;
    private Intent mServiceIntent;
    private MainFragment mMainFragment;
    private LoginManager loginManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "office.posted@gmail.com", Snackbar.LENGTH_LONG)
                        .setAction("send us email", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(Intent.ACTION_SEND);
                                i.setType("message/rfc822");
                                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"office.posted@gmail.com"});
                                i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
                                i.putExtra(Intent.EXTRA_TEXT   , "body of email");
                                try {
                                    startActivity(Intent.createChooser(i, "Send mail..."));
                                } catch (android.content.ActivityNotFoundException ex) {
                                    Toast.makeText(AdminMainActivity.this,"There are no email clients installed.",Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).show();
            }
        });
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        this.loginManager = new LoginManager(this);
        this.ctx = this;
        this.mServiceIntent = new Intent(this, LoadDataService.class);
        this.startService(this.mServiceIntent);

        this.mMainFragment = new MainFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.adminContainer, this.mMainFragment).commit();

        this.loginManager = new LoginManager(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0){
                super.onBackPressed();
            }else {
                getSupportFragmentManager().popBackStack();
            }

        }
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

        if (id == R.id.admin_nav_laptops) {
            OverviewFragment overviewFragment = new OverviewFragment();
            this.getSupportFragmentManager().beginTransaction().replace(R.id.adminContainer, overviewFragment).addToBackStack(null).commit();
        } else if (id == R.id.admin_nav_phones) {
            // show "coming soon'
        } else if (id == R.id.admin_nav_addProduct) {
            AddProductFragment fragment = new AddProductFragment();
            this.getSupportFragmentManager().beginTransaction().replace(R.id.adminContainer, fragment).commit();
        } else if (id == R.id.admin_nav_signOut) {
            loginManager.logoutUser();
            Intent intent = new Intent(this, LoginActivity.class);
            this.finish();
            this.startActivity(intent);
        } else if (id == R.id.home) {
            // show home
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onLaptopSelected(LaptopSqlite laptop) {
        Bundle bundleLaptop = new Bundle();
        bundleLaptop.putParcelable("current_laptop", laptop);
        LaptopFragment laptopFragment = new LaptopFragment();
        laptopFragment.setArguments(bundleLaptop);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.adminContainer, laptopFragment)
                .addToBackStack(null)
                .commit();
    }


    @Override
    protected void onDestroy() {
        if (this.mServiceIntent != null){
            stopService(this.mServiceIntent);
        }
        super.onDestroy();
    }
}
