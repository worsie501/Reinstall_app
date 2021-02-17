package com.example.reinstall_app.activity_classes;

import androidx.appcompat.app.AppCompatActivity;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.backendless.persistence.local.UserIdStorageFactory;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.example.reinstall_app.R;
import com.example.reinstall_app.app_data.ReinstallApplicationClass;
import com.example.reinstall_app.app_data.Resident;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.CellSignalStrength;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class ResidentProfile extends AppCompatActivity {

    TextView tvUserTotalReports, tvTrophies, tvUserName, tvUserLocation;
    Button btnUserLogout;
    ImageView ivFirstTrophy, ivSecondTrophy, ivThirdTrophy;


    private View mProgressView;
    private View mLoginFormView;
    private TextView tvLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resident_profile);

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        tvLoad = findViewById(R.id.tvLoad);


        tvUserTotalReports = findViewById(R.id.tvUserTotalReports);
        tvTrophies = findViewById(R.id.tvTrophies);
        tvUserLocation = findViewById(R.id.tvUserLocation);
        tvUserName = findViewById(R.id.tvUserName);

        btnUserLogout = findViewById(R.id.btnUserLogout);

        ivFirstTrophy = findViewById(R.id.ivFirstTrophy);
        ivSecondTrophy = findViewById(R.id.ivSecondTrophy);
        ivThirdTrophy = findViewById(R.id.ivThirdTrophy);

        this.setTitle("My Profile");

        String userObjectId = UserIdStorageFactory.instance().getStorage().get();

        Backendless.Data.of(BackendlessUser.class).findById(userObjectId, new AsyncCallback<BackendlessUser>() {
                    @Override
                    public void handleResponse(BackendlessUser response) {

                        ReinstallApplicationClass.user = response;
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        Toast.makeText(ResidentProfile.this, "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


        String whereClause = "email = '" + ReinstallApplicationClass.user.getEmail() + "'";
        int PAGESIZE = 80;
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause(whereClause);
        queryBuilder.setPageSize(PAGESIZE);

        showProgress(true);
        tvLoad.setText("Fetching profile...please wait...");


        Backendless.Persistence.of(Resident.class).find(queryBuilder, new AsyncCallback<List<Resident>>() {
            @Override
            public void handleResponse(List<Resident> response) {

                Resident resident = new Resident();

                for (int i = 0; i < response.size(); i++)
                {
                    resident.setResidentName(response.get(i).getResidentName());
                    resident.setTotalReports(response.get(i).getTotalReports());
                    resident.setCity(response.get(i).getCity());
                    resident.setSuburb(response.get(i).getSuburb());

                }


                String location;
                location = resident.getCity() + ", " +  resident.getSuburb();

                tvUserName.setText(resident.getResidentName());
                tvUserTotalReports.setText(String.valueOf(resident.getTotalReports()));
                tvUserLocation.setText(location);

                if (resident.getTotalReports() >= 10)
                {
                    ivFirstTrophy.setImageResource(R.drawable.ten);
                    tvTrophies.setText("1");
                }
                else
                {
                    ivFirstTrophy.setImageResource(R.drawable.notunlocked);
                    tvTrophies.setText("0");
                }

                if(resident.getTotalReports() >= 50)
                {
                    ivSecondTrophy.setImageResource(R.drawable.fifty);
                    tvTrophies.setText("2");
                }
                else
                {
                    ivSecondTrophy.setImageResource(R.drawable.notunlocked);
                }


                if (resident.getTotalReports() >= 100)
                {
                    ivThirdTrophy.setImageResource(R.drawable.hundred);
                    tvTrophies.setText("3");
                }
                else
                 {
                     ivThirdTrophy.setImageResource(R.drawable.notunlocked);
                 }

                showProgress(false);

            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Toast.makeText(ResidentProfile.this, "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();
                showProgress(false);
            }
        });

        btnUserLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                showProgress(true);
                tvLoad.setText("Bussy logging you out...please wait...");

                CometChat.logout(new CometChat.CallbackListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        //Toast.makeText(ResidentProfile.this, "Comet chat user logged out", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(CometChatException e) {
                        Toast.makeText(ResidentProfile.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });



                Backendless.UserService.logout(new AsyncCallback<Void>() {
                    @Override
                    public void handleResponse(Void response) {

                        startActivity(new Intent(ResidentProfile.this, CombinedLogin.class));
                        ResidentProfile.this.finish();

                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {

                        Toast.makeText(ResidentProfile.this, "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });




    }


    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });

            tvLoad.setVisibility(show ? View.VISIBLE : View.GONE);
            tvLoad.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    tvLoad.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            tvLoad.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

}