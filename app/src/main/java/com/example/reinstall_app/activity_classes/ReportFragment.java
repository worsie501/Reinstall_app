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
import android.provider.ContactsContract;
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
import androidx.cardview.widget.CardView;
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
import com.backendless.persistence.local.UserIdStorageFactory;
import com.example.reinstall_app.R;
import com.example.reinstall_app.app_data.District;
import com.example.reinstall_app.app_data.ProblemType;
import com.example.reinstall_app.app_data.ReinstallApplicationClass;
import com.example.reinstall_app.app_data.ReportedProblem;
import com.example.reinstall_app.app_data.Resident;
import com.example.reinstall_app.app_data.Suburb;
import com.example.reinstall_app.app_data.User;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static android.content.Context.RECEIVER_VISIBLE_TO_INSTANT_APPS;
import static com.example.reinstall_app.app_data.ReinstallApplicationClass.problemTypes;
import static com.example.reinstall_app.app_data.ReinstallApplicationClass.suburbList;
import static com.example.reinstall_app.app_data.ReinstallApplicationClass.user;

public class ReportFragment extends Fragment
{


    //Progres wheel vars
    private View mProgressView;
    private View mLoginFormView;
    private TextView tvLoad;


    //map vars
    private static final int ERROR_DIALOG_REQUEST = 9001;

    Button btnLocation;
    Spinner spCategory, spnrUrgency;;
    ImageButton btnPhoto;
    Button btnSubmitReport, btnCancelReport;
    EditText etDescription;
    TextView tvLat,tvLon,tvAddress, tvCity, tvSuburnLocated, tvSumDesc, tvSumType, tvSumUrgency;
    CardView cvSummary;

    int mapRequestCode=2;
    int mapResultCode=2;

    List<ProblemType> pt ;

    double y, x;
    String addressString, cityLocation, suburbConfirmed = "";
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    String photoName= y +""+ x + timeStamp;
    String photoPath = "https://backendlessappcontent.com/0DDD13FF-20A8-91F2-FFE4-4842B5764B00/BB772A4D-ACFA-47B3-B3F3-C08BA9D0707F/files/photos/";


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

        if(requestCode == mapRequestCode)
        {
            if(resultCode == mapResultCode)
            {

                y = data.getDoubleExtra("lat", 0);
                x = data.getDoubleExtra("lon", 0);
                addressString = data.getStringExtra("addressString");
                cityLocation = data.getStringExtra("cityLocation");

                tvLat.setText(String.valueOf(y));
                tvLon.setText(String.valueOf(x));
                tvAddress.setText(addressString);
                tvCity.setText(cityLocation);


                if (etDescription.getText().toString().isEmpty() || spCategory.getSelectedItem() == null || spnrUrgency.getSelectedItem() == null )
                {

                    Toast.makeText(getContext(), "Please enter all fields", Toast.LENGTH_SHORT).show();

                }
                else
                {
                    tvSumDesc.setText(etDescription.getText());
                    tvSumUrgency.setText(spnrUrgency.getSelectedItem().toString());
                    tvSumType.setText(spCategory.getSelectedItem().toString());
                    cvSummary.setVisibility(View.VISIBLE);
                }


                DataQueryBuilder queryBuilder = DataQueryBuilder.create();
                int PAGESIZE = 80;
                queryBuilder.setPageSize(PAGESIZE);

                Backendless.Persistence.of(Suburb.class).find(queryBuilder, new AsyncCallback<List<Suburb>>() {
                    @Override
                    public void handleResponse(List<Suburb> response) {

                        for(int i=0; i<response.size(); i++)
                        {

                            if(addressString.contains(response.get(i).getSuburbName()))
                            {
                                suburbConfirmed = response.get(i).getSuburbName().trim();
                                tvSuburnLocated.setText(suburbConfirmed);

                                i = response.size();
                            }
                            else
                            {
                                    tvSuburnLocated.setText("N/A");
                                    suburbConfirmed = "N/A";
                            }
                        }


                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {

                        Toast.makeText(getActivity(), "Error: "+fault.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

            }


        }
        else if(requestCode == 0) {
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
                    problem.setPhoto(photoPath + photoName);
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

        mLoginFormView = v.findViewById(R.id.login_form);
        mProgressView = v.findViewById(R.id.login_progress);
        tvLoad = v.findViewById(R.id.tvLoad);

        btnLocation = v.findViewById(R.id.btnLocation);
        spCategory =  v.findViewById(R.id.spCategory);
        btnSubmitReport=v.findViewById(R.id.btnSubmitReport);
        btnCancelReport = v.findViewById(R.id.btnCancelReport);
        etDescription=v.findViewById(R.id.etDescription);
        btnPhoto=v.findViewById(R.id.btnPhoto);
        tvLat=v.findViewById(R.id.tvLat);
        tvLon=v.findViewById(R.id.tvLon);
        tvAddress=v.findViewById(R.id.tvAddress);
        tvCity=v.findViewById(R.id.tvCity);
        tvSuburnLocated=v.findViewById(R.id.tvSuburbLocated);
        spnrUrgency=v.findViewById(R.id.spnrUrgency);
        cvSummary = v.findViewById(R.id.cvSummary);

        tvSumDesc = v.findViewById(R.id.tvSumDesc);
        tvSumType = v.findViewById(R.id.tvSumType);
        tvSumUrgency = v.findViewById(R.id.tvSumUrgency);

        cvSummary.setVisibility(View.GONE);

        List<String> urgencySpinnerArray= new ArrayList<String>();
        urgencySpinnerArray.add("Low");
        urgencySpinnerArray.add("Medium(Normal)");
        urgencySpinnerArray.add("High");
        urgencySpinnerArray.add("Critical");

        ArrayAdapter<String> urgencyAdapter=new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, urgencySpinnerArray);

        urgencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnrUrgency.setAdapter(urgencyAdapter);

        DataQueryBuilder subQueryBuilder = DataQueryBuilder.create();
        int PAGESIZE = 80;
        subQueryBuilder.setPageSize(PAGESIZE);

        Backendless.Persistence.of(Suburb.class).find(subQueryBuilder, new AsyncCallback<List<Suburb>>() {
            @Override
            public void handleResponse(List<Suburb> response) {
                ReinstallApplicationClass.suburbList = response;
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Toast.makeText(getActivity(), "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        DataQueryBuilder ptQueryBuilder = DataQueryBuilder.create();
        ptQueryBuilder.setPageSize(PAGESIZE);

        Backendless.Persistence.of(ProblemType.class).find(ptQueryBuilder, new AsyncCallback<List<ProblemType>>() {
            @Override
            public void handleResponse(List<ProblemType> response) {
                ReinstallApplicationClass.problemTypes = response;
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Toast.makeText(getActivity(), "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setPageSize(80);
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
                }
                else
                    {

                   // GeoPoint geoPoint=new GeoPoint(lon, lat);
                    String userObjectId = UserIdStorageFactory.instance().getStorage().get();
                    Backendless.Data.of(BackendlessUser.class).findById(userObjectId, new AsyncCallback<BackendlessUser>() {
                        @Override
                        public void handleResponse(BackendlessUser response) {
                            ReinstallApplicationClass.user = response;
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            Toast.makeText(getActivity(), "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                    problem.setUserName(user.getProperty("name").toString());
                    problem.setProblemType(spCategory.getSelectedItem().toString().trim());
                    problem.setDescription(etDescription.getText().toString().trim());
                    problem.setCity(cityLocation.trim());
                    problem.setSuburb(suburbConfirmed);
                    problem.setX(x); //lon
                    problem.setY(y); //lat
                    problem.setReportUrgency(spnrUrgency.getSelectedItem().toString().trim());

                    showProgress(true);
                    tvLoad.setText("Submitting new report...please wait");


                        for(int i = 0; i < problemTypes.size(); i++)
                        {

                            if(problemTypes.get(i).getProblemName().equals(spCategory.getSelectedItem().toString().trim()))
                            {

                               // Toast.makeText(getActivity(), "" + problemTypes.get(i).getObjectId(), Toast.LENGTH_SHORT).show();

                                problemTypes.get(i).setTotalProblems(problemTypes.get(i).getTotalProblems() + 1);

                                Backendless.Data.of(ProblemType.class).save(ReinstallApplicationClass.problemTypes.get(i), new AsyncCallback<ProblemType>() {
                                    @Override
                                    public void handleResponse(ProblemType response) {

                                      // Toast.makeText(getActivity(), "Increased " + response.getProblemName() + " : " + response.getTotalProblems(), Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void handleFault(BackendlessFault fault) {
                                        Toast.makeText(getActivity(), "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();
                                        showProgress(false);
                                    }
                                });
                                break;
                            }

                        }

                                    for(int i = 0; i < suburbList.size(); i++)
                                    {

                                        if(suburbList.get(i).getSuburbName().equals(suburbConfirmed))
                                        {

                                           //Toast.makeText(getActivity(), "" + suburbList.get(i).getObjectId(), Toast.LENGTH_SHORT).show();
                                            ReinstallApplicationClass.suburbList.get(i).setTotalReports(suburbList.get(i).getTotalReports() + 1);

                                            Backendless.Persistence.save(suburbList.get(i), new AsyncCallback<Suburb>() {
                                                @Override
                                                public void handleResponse(Suburb response) {
                                                   // Toast.makeText(getActivity(), "Suburb problem count increased", Toast.LENGTH_LONG).show();
                                                }

                                                @Override
                                                public void handleFault(BackendlessFault fault) {

                                                    Toast.makeText(getActivity(), "Error: "+fault.getMessage(), Toast.LENGTH_SHORT).show();
                                                    showProgress(false);
                                                }
                                            });

                                            break;
                                        }
                                    }



                                    if(user.getProperty("role").equals("Resident"))
                                    {


                                        ReinstallApplicationClass.resident.setTotalReports(ReinstallApplicationClass.resident.getTotalReports() + 1);

                                        Backendless.Persistence.save(ReinstallApplicationClass.resident, new AsyncCallback<Resident>() {
                                            @Override
                                            public void handleResponse(Resident response) {

                                            }

                                            @Override
                                            public void handleFault(BackendlessFault fault) {

                                                Toast.makeText(getActivity(), "Error: "+fault.getMessage(), Toast.LENGTH_SHORT).show();
                                                showProgress(false);
                                            }
                                        });

                                    }

                        Backendless.Persistence.save(problem, new AsyncCallback<ReportedProblem>() {
                            @Override
                            public void handleResponse(ReportedProblem response) {
                                Toast.makeText(getActivity(), "Report Submitted!", Toast.LENGTH_SHORT).show();
                                cvSummary.setVisibility(View.GONE);

                                tvLoad.setText("Report Submitted...");
                                showProgress(false);
                            }

                            @Override
                            public void handleFault(BackendlessFault fault) {
                                Toast.makeText(getActivity(), "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();
                                showProgress(false);
                            }
                        });

                                }

                    spCategory.setSelection(0);
                    etDescription.setText(null);

                }
            });


        btnCancelReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                cvSummary.setVisibility(View.GONE);
                spCategory.setSelection(0);
                spnrUrgency.setSelection(0);
                etDescription.setText(null);


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
