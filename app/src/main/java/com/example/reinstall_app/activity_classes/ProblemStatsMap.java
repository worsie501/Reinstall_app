package com.example.reinstall_app.activity_classes;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.example.reinstall_app.R;
import com.example.reinstall_app.app_data.ProblemType;
import com.example.reinstall_app.app_data.ReinstallApplicationClass;
import com.example.reinstall_app.app_data.ReportedProblem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class ProblemStatsMap extends AppCompatActivity  implements OnMapReadyCallback {


    GoogleMap mapCurrent;
    MapView statsMap;
    Button btnCloseStatsMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem_stats_map);

        statsMap = (MapView) findViewById(R.id.mStatsMap);
        btnCloseStatsMap = findViewById(R.id.btnCloseStatsMap);

        final int index = getIntent().getIntExtra("index", 0);

        if(statsMap != null)
        {
            statsMap.onCreate(null);
            statsMap.onResume();
            statsMap.getMapAsync(ProblemStatsMap.this);
        }


        statsMap.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {

                String whereClause = "problemType = '" + ReinstallApplicationClass.problemTypes.get(index).getProblemName() + "'";
                DataQueryBuilder queryBuilder = DataQueryBuilder.create();
                queryBuilder.setWhereClause(whereClause);

                int PAGESIZE = 80;
                queryBuilder.setPageSize(PAGESIZE);

                Backendless.Data.of(ReportedProblem.class).find(queryBuilder, new AsyncCallback<List<ReportedProblem>>() {
                    @Override
                    public void handleResponse(List<ReportedProblem> response) {


                        float zoomLevel = 14.0f;

                        for(int i = 0; i < response.size(); i++)
                        {

                            LatLng latLng = new LatLng(response.get(i).getY(), response.get(i).getX());

                            MarkerOptions markerOptions = new MarkerOptions();

                            markerOptions.position(latLng);

                            mapCurrent.addMarker(new MarkerOptions().position(latLng));

                            mapCurrent.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
                        }

                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {

                        Toast.makeText(ProblemStatsMap.this, "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();


                    }
                });


            }
        });


    btnCloseStatsMap.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            ProblemStatsMap.this.finish();

        }
    });


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mapCurrent = googleMap;

    }
}