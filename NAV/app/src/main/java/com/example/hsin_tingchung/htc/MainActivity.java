package com.example.hsin_tingchung.htc;

import android.app.FragmentManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.hsin_tingchung.nav.R;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public static final String TAG = "MainActivity";
    private GoogleApiClient client;
    private int page = 1;
    private Fragment functionFragment;
    final String bluetooth_Chat_fragment_TAG = "bluetooth_Chat_fragment_TAG";
    final String GPS_fragment_TAG = "GPS_fragment_TAG";
    String functionFragment_TAG = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
/*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
*/
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            BluetoothChatFragment fragmentBT = new BluetoothChatFragment();
            fragmentBT.setTargetFragment(fragmentBT, 0);
            transaction.add(fragmentBT, bluetooth_Chat_fragment_TAG);
            transaction.replace(R.id.sample_content_fragment, fragmentBT);
            transaction.commit();

            FragmentTransaction transactionGPS = getSupportFragmentManager().beginTransaction();
            GpsFragment fragmentGPS = new GpsFragment();
            fragmentGPS.setTargetFragment(fragmentGPS, 0);
            transactionGPS.add(fragmentGPS, GPS_fragment_TAG);
            transactionGPS.replace(R.id.gps_container, fragmentGPS);
            transactionGPS.commit();

            FragmentTransaction transactionAcco = getSupportFragmentManager().beginTransaction();
            AccountView fragmentAcco = new AccountView();
            fragmentAcco.setTargetFragment(fragmentAcco, 0);
            transactionAcco.add(fragmentAcco, "AccountView_TAG");
            transactionAcco.replace(R.id.fragment_container, fragmentAcco);
            transactionAcco.commit();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        switch (item.getItemId()) {
            case R.id.nav_account:
                functionFragment = AccountView.newInstance("A","A");
                functionFragment_TAG = "AccountView_TAG";
                page = 1;
                break;
            case R.id.nav_budget:
                functionFragment = BudgetView.newInstance("B", "B");
                functionFragment_TAG = "BudgetView_TAG";
                page = 2;
                break;
            case R.id.nav_statistics:
                functionFragment = StatisticsView.newInstance("S","S");
                functionFragment_TAG = "StatisticsView_TAG";
                page = 3;
                break;
            case R.id.nav_setting:
                functionFragment = TagEdit.newInstance("T","T");
                functionFragment_TAG = "TagEdit_TAG";
                page = 4;
                break;
            case R.id.nav_share:
                page = 5;
                functionFragment = BlankFragment.newInstance(page);
                break;
            case R.id.nav_send:
                page = 6;
                functionFragment = BlankFragment.newInstance(page);
                break;
        }

        changeFragment(functionFragment);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void changeFragment(Fragment fragment){
        fragment.setTargetFragment(fragment, 0);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(fragment, functionFragment_TAG);
        transaction.replace(R.id.fragment_container, fragment);

        int backStackCount = getSupportFragmentManager().getBackStackEntryCount();
        for (int i = 0; i < backStackCount; i++) {
            int backStackId = getSupportFragmentManager().getBackStackEntryAt(i).getId(); // Get the back stack fragment id.
            getSupportFragmentManager().popBackStack(backStackId, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        transaction.commitAllowingStateLoss();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.hsin_tingchung.nav/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.hsin_tingchung.nav/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
