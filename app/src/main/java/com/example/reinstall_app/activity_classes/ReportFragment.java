package com.example.reinstall_app.activity_classes;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.files.BackendlessFile;
import com.backendless.geo.GeoPoint;
import com.backendless.persistence.DataQueryBuilder;
import com.example.reinstall_app.R;
import com.example.reinstall_app.app_data.District;
import com.example.reinstall_app.app_data.ProblemType;
import com.example.reinstall_app.app_data.ReportedProblem;
import com.example.reinstall_app.app_data.Suburb;
import com.example.reinstall_app.app_data.User;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Date;
import java.util.List;

import static android.content.Context.RECEIVER_VISIBLE_TO_INSTANT_APPS;
import static com.example.reinstall_app.app_data.ReinstallApplicationClass.user;

public class ReportFragment extends Fragment
{

    //map vars
    private static final int ERROR_DIALOG_REQUEST = 9001;

    Button btnLocation;
    Spinner spCategory;
    ImageButton btnPhoto;
    Button btnSubmitReport;
    EditText etDescription;
    TextView tvLat,tvLon,tvAddress, tvCity, tvSuburnLocated;

    int mapRequestCode=2;
    int mapResultCode=2;

    double y, x;
    String addressString, cityLocation, suburbConfirmed="";
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    String photoName= y +""+ x + timeStamp;
    String photoPath = "https://backendlessappcontent.com/75A538D0-1849-8BBB-FFEF-9D766ECC4500/FAB6E7BA-ED2F-4AC8-B356-CA99442F20A4/files/photos/";

    final ReportedProblem problem = new ReportedProblem();

    View v;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        v = inflater.inflate(R.layout.fragment_report, container, false);


        return v;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==mapRequestCode)
        {
            if(resultCode == mapResultCode)
            {


                y=data.getDoubleExtra("lat", 0);
                x=data.getDoubleExtra("lon", 0);
                addressString=data.getStringExtra("addressString");
                cityLocation=data.getStringExtra("cityLocation");

                tvLat.setText(String.valueOf(y));
                tvLon.setText(String.valueOf(x));
                tvAddress.setText("Address: "+addressString);
                tvCity.setText("City: "+cityLocation);

                final DataQueryBuilder queryBuilder = DataQueryBuilder.create();
                //queryBuilder.setWhereClause("suburbName");

                Backendless.Persistence.of(Suburb.class).find(queryBuilder, new AsyncCallback<List<Suburb>>() {
                    @Override
                    public void handleResponse(List<Suburb> response) {

                        for(int i=0; i<response.size(); i++)
                        {
                         //   if(response.get(i).getSuburbName().isEmpty())
                         //   {
                         //      Toast.makeText(getActivity(), "No Data Found!!!", Toast.LENGTH_SHORT).show();
                           //     tvSuburnLocated.setText("Eks fucked!!!");
                          //  }
                          //  else if(addressString.toLowerCase().contains(response.get(i).getSuburbName()))
                          //  {
                           //     suburbConfirmed=response.get(i).getSuburbName().trim();
                           //     tvSuburnLocated.setText(suburbConfirmed);
                           // }

                            if(addressString.contains(response.get(i).getSuburbName()))
                            {
                                suburbConfirmed=response.get(i).getSuburbName().trim();
                                tvSuburnLocated.setText(suburbConfirmed);

                               // response.get(i).setTotalReports(+1);
                            }
                        }
                        if(suburbConfirmed.isEmpty())
                        {
                            tvSuburnLocated.setText("N/A");
                        }

                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {

                        Toast.makeText(getActivity(), "Error: "+fault.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

            }


        }
        else if(requestCode==0) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            Backendless.Files.Android.upload( photo,
                    Bitmap.CompressFormat.PNG,
                    100,
                    photoName,
                    "photos",
                    new AsyncCallback<BackendlessFile>()
                    {
                        @Override
                        public void handleResponse( final BackendlessFile backendlessFile )
                        {
                        }

                        @Override
                        public void handleFault( BackendlessFault backendlessFault )
                        {
                            Toast.makeText( getActivity(),
                                    backendlessFault.toString(),
                                    Toast.LENGTH_SHORT ).show();
                        }
                    });
            Backendless.Persistence.of(ReportedProblem.class).save(problem, new AsyncCallback<ReportedProblem>() {
                @Override
                public void handleResponse(ReportedProblem response) {
                    problem.setPhoto(photoPath+photoName);
                }

                @Override
                public void handleFault(BackendlessFault fault) {

                }
            });
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        btnLocation = v.findViewById(R.id.btnLocation);
        spCategory =  v.findViewById(R.id.spCategory);
        btnSubmitReport=v.findViewById(R.id.btnSubmitReport);
        etDescription=v.findViewById(R.id.etDescription);
        btnPhoto=v.findViewById(R.id.btnPhoto);
        tvLat=v.findViewById(R.id.tvLat);
        tvLon=v.findViewById(R.id.tvLon);
        tvAddress=v.findViewById(R.id.tvAddress);
        tvCity=v.findViewById(R.id.tvCity);
        tvSuburnLocated=v.findViewById(R.id.tvSuburbLocated);


        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setGroupBy("problemName");

        Backendless.Data.of(ProblemType.class).find(queryBuilder, new AsyncCallback<List<ProblemType>>() {
            @Override
            public void handleResponse(List<ProblemType> response) {

                ArrayAdapter<ProblemType> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, response);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                spCategory.setAdapter(adapter);
            }

            @Override
            public void handleFault(BackendlessFault fault) {

                Toast.makeText(getActivity(), "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        btnSubmitReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (etDescription.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "Please enter a short description of the problem", Toast.LENGTH_SHORT).show();
                } else {

                   // GeoPoint geoPoint=new GeoPoint(lon, lat);


                    problem.setProblemType(spCategory.getSelectedItem().toString().trim());
                    problem.setDescription(etDescription.getText().toString().trim());
                    problem.setCity(cityLocation.trim());
                    problem.setSuburb(suburbConfirmed);
                    problem.setX(x); //lon
                    problem.setY(y); //lat



                    Backendless.Persistence.save(problem, new AsyncCallback<ReportedProblem>() {
                        @Override
                        public void handleResponse(ReportedProblem response) {
                            Toast.makeText(getActivity(), "Report Submitted!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {

                            Toast.makeText(getActivity(), "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
                    spCategory.setSelection(0);
                    etDescription.setText(null);
                }
            }
        });

        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,0);
            }
        });


        //map

        if(isServicesOK())
        {
            init();
        }

    }

    private void init()
    {
        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MapActivity.class);
                startActivityForResult(intent, mapRequestCode);


            }
        });
    }


    public boolean isServicesOK(){


        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getActivity());

        if(available == ConnectionResult.SUCCESS)
        {

            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available))
        {
            //error occured but could be resolved

            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else
        {
            Toast.makeText(getActivity(), "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

}
