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

    int mapRequestCode=2;
    int mapResultCode=2;

    String lat, lon, addressString;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.fragment_report, container, false);

        btnLocation = v.findViewById(R.id.btnLocation);
        spCategory =  v.findViewById(R.id.spCategory);
        btnSubmitReport=v.findViewById(R.id.btnSubmitReport);
        etDescription=v.findViewById(R.id.etDescription);
        btnPhoto=v.findViewById(R.id.btnPhoto);


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

                    final ReportedProblem problem = new ReportedProblem();
                    problem.setProblemType(spCategory.getSelectedItem().toString().trim());
                    problem.setDescription(etDescription.getText().toString().trim());

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

        return v;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==mapRequestCode)
        {
            if(resultCode == mapResultCode)
            {

                lat=data.getStringExtra("lat");
                lon=data.getStringExtra("lon");
                addressString=data.getStringExtra("addressString");

            }


        }
        else if(requestCode==0) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

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
