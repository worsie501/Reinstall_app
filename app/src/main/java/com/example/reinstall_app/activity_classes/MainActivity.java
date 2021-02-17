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
import android.app.AlertDialog;
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
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.backendless.persistence.local.UserIdStorageFactory;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.example.reinstall_app.R;
import com.example.reinstall_app.app_data.ProblemType;
import com.example.reinstall_app.app_data.ReinstallApplicationClass;
import com.example.reinstall_app.app_data.ReportedProblem;
import com.example.reinstall_app.app_data.Resident;
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

public class MainActivity extends AppCompatActivity implements HotSpotAdapter.ItemClicked, FeedAdapter.FeedItemClicked, StatsAdapter.StatsItemClicked, LogoutDialog.LogoutDialogListener {

    private static final String TAG = "MainActivity";

    ActionBar actionBar;
    ImageButton btnChats;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initializations

        btnChats = findViewById(R.id.btnChats);

        actionBar = getSupportActionBar();
        actionBar.setTitle("  |  Dashboard");
        actionBar.setIcon(R.drawable.ic_relogo);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_Container, new DashboardFragment()).commit();

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        bottomNav.setOnNavigationItemSelectedListener(navListner);

        String userObjectId = UserIdStorageFactory.instance().getStorage().get();

        int PAGESIZE = 80;
        DataQueryBuilder ptQueryBuilder = DataQueryBuilder.create();
        ptQueryBuilder.setPageSize(PAGESIZE);

        Backendless.Persistence.of(ProblemType.class).find(ptQueryBuilder, new AsyncCallback<List<ProblemType>>() {
            @Override
            public void handleResponse(List<ProblemType> response) {
                ReinstallApplicationClass.problemTypes = response;
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Toast.makeText(MainActivity.this, "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        DataQueryBuilder rQueryBuilder = DataQueryBuilder.create();
        rQueryBuilder.setPageSize(PAGESIZE);

        Backendless.Persistence.of(ReportedProblem.class).find(rQueryBuilder, new AsyncCallback<List<ReportedProblem>>() {
                    @Override
                    public void handleResponse(List<ReportedProblem> response) {
                       ReinstallApplicationClass.reportedProblems = response;
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        Toast.makeText(MainActivity.this, "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


                Backendless.Data.of(BackendlessUser.class).findById(userObjectId, new AsyncCallback<BackendlessUser>() {
                    @Override
                    public void handleResponse(BackendlessUser response) {
                        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
                        ReinstallApplicationClass.user = response;

                        if (response.getProperty("role").equals("Resident")) {
                            bottomNav.getMenu().getItem(4).setVisible(false);

                            int PAGESIZE = 80;
                            String whereClause = "email = '" + ReinstallApplicationClass.user.getEmail() + "'";
                            DataQueryBuilder queryBuilder = DataQueryBuilder.create();
                            queryBuilder.setWhereClause(whereClause);
                            queryBuilder.setPageSize(PAGESIZE);

                            Backendless.Persistence.of(Resident.class).find(queryBuilder, new AsyncCallback<List<Resident>>() {
                                @Override
                                public void handleResponse(List<Resident> response) {

                                    ReinstallApplicationClass.resident = response.get(0);

                                }

                                @Override
                                public void handleFault(BackendlessFault fault) {
                                    Toast.makeText(MainActivity.this, "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                        }

                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {

                        Toast.makeText(MainActivity.this, "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });



        btnChats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, ChatGroupsList.class);
                startActivity(intent);

            }
        });




    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {

            case R.id.logout:

                openDialog();

                break;

                    case R.id.profile:
                        Intent intent = new Intent(MainActivity.this, ResidentProfile.class);
                        startActivity(intent);
                        break;

        }

        return super.onOptionsItemSelected(item);
    }


    public void openDialog()
    {
        LogoutDialog dialog = new LogoutDialog();
        dialog.show(getSupportFragmentManager(), "logout dialog");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem menuItem = menu.findItem(R.id.profile);

        ////////////////////////////////////////////////////////////////////////////////////////////// FIX
        if(ReinstallApplicationClass.user.getProperty("role").equals("Resident"))
        {
            menuItem.setVisible(true);
        }
        else
        {
            menuItem.setVisible(false);
        }

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

    @Override
    public void onLogoutClicked() {

        Toast.makeText(MainActivity.this, "busy logging out...please wait...", Toast.LENGTH_LONG).show();

        CometChat.logout(new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {
                Toast.makeText(MainActivity.this, "Comet chat user logged out", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(CometChatException e) {
                Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });


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
}