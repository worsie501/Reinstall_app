package com.example.reinstall_app.activity_classes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.example.reinstall_app.R;
import com.example.reinstall_app.app_data.ReinstallApplicationClass;
import com.example.reinstall_app.app_data.ReportedProblem;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

public class SuburbHotspotMap extends AppCompatActivity implements OnMapReadyCallback{

    GoogleMap mapCurrent;
    MapView hotspotMap;
    Button btnCloseHotspot;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suburb_hotspot_map);

        hotspotMap = (MapView) findViewById(R.id.mHotspotMap);
        btnCloseHotspot = findViewById(R.id.btnCloseHotspot);

       final int index = getIntent().getIntExtra("index", 0);

        if(hotspotMap != null)
        {
            hotspotMap.onCreate(null);
            hotspotMap.onResume();
            hotspotMap.getMapAsync(SuburbHotspotMap.this);
        }


        hotspotMap.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {

                String whereClause = "suburb = '" + ReinstallApplicationClass.hotspotList.get(index).getSuburbName() + "' AND resolved = false AND fakeReport = false";
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

                            mapCurrent.addMarker(new MarkerOptions().position(latLng)
                            .title(response.get(i).getProblemType())
                            .snippet("Urgency: " + response.get(i).getReportUrgency())
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                            mapCurrent.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
                        }


                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {

                        Toast.makeText(SuburbHotspotMap.this, "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

            }
        });


        btnCloseHotspot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SuburbHotspotMap.this.finish();

            }
        });


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mapCurrent = googleMap;

    }
}