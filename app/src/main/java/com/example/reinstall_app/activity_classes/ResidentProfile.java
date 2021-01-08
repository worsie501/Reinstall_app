package com.example.reinstall_app.activity_classes;

import androidx.appcompat.app.AppCompatActivity;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.backendless.persistence.local.UserIdStorageFactory;
import com.example.reinstall_app.R;
import com.example.reinstall_app.app_data.ReinstallApplicationClass;
import com.example.reinstall_app.app_data.Resident;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.os.Bundle;
import android.telephony.CellSignalStrength;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class ResidentProfile extends AppCompatActivity {

    TextView tvUserTotalReports, tvTrophies, tvUserName, tvUserLocation;
    Button btnUserLogout;
    ImageView ivFirstTrophy, ivSecondTrophy, ivThirdTrophy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resident_profile);

        tvUserTotalReports = findViewById(R.id.tvUserTotalReports);
        tvTrophies = findViewById(R.id.tvTrophies);
        tvUserLocation = findViewById(R.id.tvUserLocation);
        tvUserName = findViewById(R.id.tvUserName);

        btnUserLogout = findViewById(R.id.btnUserLogout);

        ivFirstTrophy = findViewById(R.id.ivFirstTrophy);
        ivSecondTrophy = findViewById(R.id.ivSecondTrophy);
        ivThirdTrophy = findViewById(R.id.ivThirdTrophy);

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


            }

            @Override
            public void handleFault(BackendlessFault fault) {

            }
        });





    }
}