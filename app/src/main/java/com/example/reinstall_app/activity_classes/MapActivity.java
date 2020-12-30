package com.example.reinstall_app.activity_classes;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.backendless.geo.GeoPoint;
import com.example.reinstall_app.R;
import com.example.reinstall_app.app_data.ReportedProblem;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;

    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;

    //widgets
    private EditText mSearchText;
    private ImageView mGps;
    private Button btnReturn;

    //vars
    private boolean mLocationPermissionGranted = false;
    private static final int LOCATION_PERMISSION_GRANTED_REQUEST_CODE = 1234;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private String lat, lon, addressString;


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(mLocationPermissionGranted)
        {
            getDeviceLocation();

            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
            {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
        // click to add marker
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                GeoPoint geoPoint = new GeoPoint(latLng.latitude,latLng.longitude);
                mMap.clear();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
                mMap.addMarker(markerOptions);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mSearchText=(EditText) findViewById(R.id.input_search);
        mGps=(ImageView) findViewById(R.id.ic_gps);
        btnReturn=(Button) findViewById(R.id.btnReturn);

        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MapActivity.this, MainActivity.class);

                int mapResultCode=2;

                intent.putExtra("lat", lat.trim());
                intent.putExtra("lon", lon.trim());
                intent.putExtra("addressString", addressString.trim());
                setResult(mapResultCode, intent);

                MapActivity.this.finish();
            }
        });

        getLocationPermission();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLastKnownLocation();

        init();

    }

    private  void init()
    {
        Log.d("TAG", "init: initializing");

       // AutocompleteSessionToken autocompleteSessionToken;
       // autocompleteSessionToken= AutocompleteSessionToken.newInstance();
        // PlacesClient placesClient;
       //placesClient= Places.createClient(getApplicationContext());

       // final PlaceAutocompleteAdapterNew mAdapter;
       // mAdapter = new PlaceAutocompleteAdapterNew(this, placesClient,autocompleteSessionToken);



       // PlaceAutocompleteAdapterNew=new PlaceAutocompleteAdapterNew(this, placesClient, autocompleteSessionToken);

      //  mSearchText.setAdapter(PlaceAutocompleteAdapterNew);

        //crash met adapter

                mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || keyEvent.getAction() == KeyEvent.ACTION_DOWN || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
                    //execute our method for searching
                    geoLocate();
                }
                return false;
            }
        });

        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TAG", "onClick: clicked gps icon");
                getDeviceLocation();
            }
        });
        hideSoftKeyboard();
    }

    private void geoLocate()
    {
        Log.d("TAG", "geoLocate: geolocating");

        String searchString=mSearchText.getText().toString().trim();

        Geocoder geocoder = new Geocoder(MapActivity.this);
        List<Address> list=new ArrayList<>();
        try {
            list=geocoder.getFromLocationName(searchString, 1);
        }catch (IOException e)
        {
            Log.e("TAG", "geoLocate: IOException: " + e.getMessage());
        }

        if(list.size()>0)
        {
            Address address=list.get(0);

            Log.d("TAG", "geoLocate: found a location: "+address.toString());

            float zoomLevel=16.0f;

           LatLng latLng=new LatLng(address.getLatitude(), address.getLongitude());

           moveCamera(latLng, zoomLevel, address.getAddressLine(0));
        }

    }

    private void moveCamera(LatLng latLng, float zoom, String title)
    {
        zoom=16f;

        Log.d("TAG", "moving the camera to: lat:"+latLng.latitude+", lng: "+latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        MarkerOptions options=new MarkerOptions()
                .position(latLng)
                .title(title);

        mMap.addMarker(options);

        hideSoftKeyboard();
    }

    private void getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }


        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {

            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if(task.isSuccessful())
                {
                    Location location = task.getResult();
                    GeoPoint geoPoint = new GeoPoint(location.getLatitude(),location.getLongitude());


                    LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    mMap.clear();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10f));
                    mMap.addMarker(markerOptions);
                }
            }
        });
    }


    private void getDeviceLocation()
    {
        Log.d("TAG", "getDeviceLocation: getting the devices current location");

        fusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(this);

        try{
            if(mLocationPermissionGranted)
            {
                final Task location=fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful())
                        {
                            float zoomLevel=16.0f;

                            Geocoder geocoder = new Geocoder(MapActivity.this);
                            List<Address> list=new ArrayList<>();

                            Log.d("TAG", "onComplete: found location!");
                            Location currentLocation=(Location)task.getResult();

                            try {
                                list=geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            String address = list.get(0).getAddressLine(0);
                            String city = list.get(0).getLocality();
                            String state = list.get(0).getAdminArea();
                            String country = list.get(0).getCountryName();
                            String postalCode = list.get(0).getPostalCode();
                            String knownName = list.get(0).getFeatureName();
                            String addressLine=list.get(0).getAddressLine(0);

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),zoomLevel, addressLine);
                        }
                        else
                        {
                            Log.d("TAG", "onComplete: current location is null");
                            Toast.makeText(MapActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e)
        {
            Log.e("TAG", "getDeviceLocation: SecurityException: "+e.getMessage());
        }

    }

    private void getLocationPermission(){
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(), COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                mLocationPermissionGranted = true;
                initMap();
            }
            else
            {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_GRANTED_REQUEST_CODE);
            }
        }
        else
        {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_GRANTED_REQUEST_CODE);
        }


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;

        switch (requestCode)
        {

            case LOCATION_PERMISSION_GRANTED_REQUEST_CODE:{
                if(grantResults.length > 0 )
                {
                    for(int i = 0; i < grantResults.length; i++)
                    {
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED)
                        {
                            mLocationPermissionGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionGranted = true;
                    initMap();
                }
            }

        }


    }

    private void initMap()
    {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(MapActivity.this);
    }

        private void hideSoftKeyboard()
        {
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }
}