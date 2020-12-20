package com.example.reinstall_app.activity_classes;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.example.reinstall_app.R;
import com.example.reinstall_app.app_data.District;
import com.example.reinstall_app.app_data.Municipality;
import com.example.reinstall_app.app_data.Province;

import java.util.ArrayList;
import java.util.List;

public class MunicipalityRegister extends AppCompatActivity {

    private View mProgressView;
    private View mLoginFormView;
    private TextView tvLoad;
    String role="Municipality";

    EditText etMunicipalityName, etMunicipalityRegisterEmail, etMunicipalityRegisterPassword, etConfirmMunicipalityPassword;
    Button btnMunicipalityRegister;
    Spinner spnrDistrict, spnrProvince;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_municipality_register);

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        tvLoad = findViewById(R.id.tvLoad);

        etMunicipalityName = findViewById(R.id.etMunicipalityName);
        etMunicipalityRegisterEmail = findViewById(R.id.etMunicipalityRegisterEmail);
        etMunicipalityRegisterPassword = findViewById(R.id.etMunicipalityRegisterPassword);
        etConfirmMunicipalityPassword = findViewById(R.id.etConfirmMunicipalityPassword);
        btnMunicipalityRegister = findViewById(R.id.btnMunicipalityRegister);
        spnrDistrict = findViewById(R.id.spnrDistrict);
        spnrProvince = findViewById(R.id.spnrProvince);

        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setGroupBy("districtName");

        Backendless.Persistence.of(District.class).find(queryBuilder, new AsyncCallback<List<District>>() {
            @Override
            public void handleResponse(List<District> response) {

                ArrayAdapter<District> arrayAdapter = new ArrayAdapter<>(MunicipalityRegister.this,android.R.layout.simple_list_item_1,response);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spnrDistrict.setAdapter(arrayAdapter);
            }

            @Override
            public void handleFault(BackendlessFault fault) {

                Toast.makeText(MunicipalityRegister.this, "Error: "+fault, Toast.LENGTH_SHORT).show();

            }
        });

       /* final List<String> districts=new ArrayList<>();
        districts.add("One");
        districts.add("Two");
        districts.add("Three");

        ArrayAdapter<String> adapter1=new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, districts);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnrDistrict.setAdapter(adapter1);*/

        btnMunicipalityRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(etMunicipalityName.getText().toString().isEmpty()||etMunicipalityRegisterEmail.getText().toString().isEmpty()||etMunicipalityRegisterPassword.getText().toString().isEmpty())
                {
                    Toast.makeText(MunicipalityRegister.this, "Please enter all fields!", Toast.LENGTH_SHORT).show();
                    Toast.makeText(MunicipalityRegister.this, etMunicipalityRegisterEmail.getText().toString().trim(), Toast.LENGTH_SHORT).show();
                }

                else {

                    if (etMunicipalityRegisterPassword.getText().toString().trim().equals(etConfirmMunicipalityPassword.getText().toString().trim())) {


                        BackendlessUser user = new BackendlessUser();
                        user.setEmail(etMunicipalityRegisterEmail.getText().toString().trim());
                        user.setPassword(etMunicipalityRegisterPassword.getText().toString().trim());
                        user.setProperty("name", etMunicipalityName.getText().toString().trim());
                        user.setProperty("role", role);
                        user.setProperty("district", spnrDistrict.getSelectedItem().toString().trim());


                        tvLoad.setText("Registering...Please wait...");
                        showProgress(true);


                        Backendless.UserService.register(user, new AsyncCallback<BackendlessUser>() {
                            @Override
                            public void handleResponse(BackendlessUser response) {
                                Toast.makeText(MunicipalityRegister.this, "Municipality Registered!", Toast.LENGTH_SHORT).show();

                               Municipality municipality = new Municipality();
                                municipality.setEmail(etMunicipalityRegisterEmail.getText().toString().trim());
                                municipality.setPassword(etMunicipalityRegisterPassword.getText().toString().trim());
                                municipality.setMunicipalityName(etMunicipalityName.getText().toString().trim());
                                municipality.setDistrictName(spnrDistrict.getSelectedItem().toString().trim());
                                //municipality.setProvince(spnrDistrict.getSelectedItem().toString().trim());


                                Backendless.Persistence.save(municipality, new AsyncCallback<Municipality>() {
                            @Override
                            public void handleResponse(Municipality response) {
                                Toast.makeText(MunicipalityRegister.this, "Municipality Registered!", Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void handleFault(BackendlessFault fault) {
                                Toast.makeText(MunicipalityRegister.this, "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();
                                showProgress(false);
                            }
                        });

                                MunicipalityRegister.this.finish();
                            }

                            @Override
                            public void handleFault(BackendlessFault fault) {
                                Toast.makeText(MunicipalityRegister.this, "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();
                                showProgress(false);
                            }
                        });


                    } else {
                        Toast.makeText(MunicipalityRegister.this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
                    }
                }

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