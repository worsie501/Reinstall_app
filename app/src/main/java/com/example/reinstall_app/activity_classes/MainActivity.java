package com.example.reinstall_app.activity_classes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.backendless.persistence.local.UserIdStorageFactory;
import com.example.reinstall_app.R;
import com.example.reinstall_app.app_data.ProblemType;
import com.example.reinstall_app.app_data.ReinstallApplicationClass;
import com.example.reinstall_app.app_data.Suburb;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class MainActivity extends AppCompatActivity implements HotSpotAdapter.ItemClicked, FeedAdapter.FeedItemClicked, StatsAdapter.StatsItemClicked{

    private static final String TAG = "MainActivity";

    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actionBar = getSupportActionBar();
        actionBar.setTitle("  |  Dashboard");
        actionBar.setIcon(R.drawable.ic_relogo);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_Container, new DashboardFragment()).commit();


        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        bottomNav.setOnNavigationItemSelectedListener(navListner);


        String userObjectId = UserIdStorageFactory.instance().getStorage().get();

        Backendless.Data.of(BackendlessUser.class).findById(userObjectId, new AsyncCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser response) {
                BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

                if(response.getProperty("role").equals("Resident"))
                {
                    bottomNav.getMenu().getItem(4).setVisible(false);
                }

            }

            @Override
            public void handleFault(BackendlessFault fault) {

                Toast.makeText(MainActivity.this, "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        switch (item.getItemId())
        {

            case R.id.logout:
                Toast.makeText(MainActivity.this, "busy logging out...please wait...", Toast.LENGTH_LONG).show();

                Backendless.UserService.logout(new AsyncCallback<Void>() {
                    @Override
                    public void handleResponse(Void response) {
                        Toast.makeText(MainActivity.this, "User signed out successfully...", Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(MainActivity.this, RoleSelection.class));
                        MainActivity.this.finish();
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {

                        Toast.makeText(MainActivity.this, "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);

        return super.onCreateOptionsMenu(menu);

    }

    private  BottomNavigationView.OnNavigationItemSelectedListener navListner =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;
                    switch (menuItem.getItemId()){
                        case R.id.nav_home:
                            selectedFragment = new FeedFragment();
                            actionBar = getSupportActionBar();
                            actionBar.setTitle("  |  Feed");
                            break;
                        case R.id.nav_record:
                            selectedFragment = new ReportFragment();
                            actionBar = getSupportActionBar();
                            actionBar.setTitle("  |  Report");
                            break;
                        case R.id.nav_stats:
                            selectedFragment = new StatisticsFragment();
                            actionBar = getSupportActionBar();
                            actionBar.setTitle("  |  Statistics");
                            break;
                        case R.id.nav_categories:
                            selectedFragment = new CategoryFragment();
                            actionBar = getSupportActionBar();
                            actionBar.setTitle("  |  Categories");
                            break;
                        case R.id.nav_dashboard:
                            selectedFragment = new DashboardFragment();
                            actionBar = getSupportActionBar();
                            actionBar.setTitle("  |  Dashboard");
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_Container, selectedFragment).commit();

                    return true;
                }
            };


    @Override
    public void onItemClicked(int index) {

        Intent intent = new Intent(MainActivity.this, SuburbHotspotMap.class);
        intent.putExtra("index", index);
        startActivity(intent);

    }

    @Override
    public void onFeedItemClicked(int index) {

    }

    @Override
    public void onStatsItemClicked(int index) {

    }
}