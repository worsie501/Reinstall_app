package com.example.reinstall_app.activity_classes;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.example.reinstall_app.R;
import com.example.reinstall_app.app_data.Resident;

public class ResidentRegister extends AppCompatActivity {

    private View mProgressView;
    private View mLoginFormView;
    private TextView tvLoad;

    EditText etResidentName,etResidentRegisterEmail, etResidentRegisterPassword, etConfirmResidentPassword;
    Spinner  spnrCity, spnrSuburb;
    Button btnResidentRegister;
    String role="Resident";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resident_register);

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        tvLoad = findViewById(R.id.tvLoad);

        etResidentName=findViewById(R.id.etResidentName);
        etResidentRegisterEmail=findViewById(R.id.etResidentRegisterEmail);
        etResidentRegisterPassword=findViewById(R.id.etResidentRegisterPasword);
        etConfirmResidentPassword=findViewById(R.id.etConfirmResidentPassword);
        spnrCity=findViewById(R.id.spnrResidentCity);
        spnrSuburb=findViewById(R.id.spnrResidentSuburb);
        btnResidentRegister=findViewById(R.id.btnResidentRegister);

        btnResidentRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(etResidentName.getText().toString().isEmpty()||etResidentRegisterEmail.getText().toString().isEmpty()||etResidentRegisterPassword.getText().toString().isEmpty())
                {
                    Toast.makeText(ResidentRegister.this, "Please enter all fields!", Toast.LENGTH_SHORT).show();

                }

                else {

                    if (etResidentRegisterPassword.getText().toString().trim().equals(etConfirmResidentPassword.getText().toString().trim())) {



                    BackendlessUser user = new BackendlessUser();
                    user.setEmail(etResidentRegisterEmail.getText().toString().trim());
                    user.setPassword(etResidentRegisterPassword.getText().toString().trim());
                    user.setProperty("name", etResidentName.getText().toString().trim());
                    user.setProperty("role", role);

                        tvLoad.setText("Registering...Please wait...");
                        showProgress(true);

                    Backendless.UserService.register(user, new AsyncCallback<BackendlessUser>() {
                        @Override
                        public void handleResponse(BackendlessUser response) {
                            Toast.makeText(ResidentRegister.this, "Resident Registered!", Toast.LENGTH_SHORT).show();

                            Resident resident = new Resident();

                            resident.setResidentName(etResidentName.getText().toString().trim());
                            resident.setEmail(etResidentRegisterEmail.getText().toString().trim());
                            // resident.setCity(spnrCity.getSelectedItem().toString().trim());
                            // resident.setSuburb(spnrSuburb.getSelectedItem().toString().trim());
                            resident.setUserPassword(etResidentRegisterPassword.getText().toString().trim());
                            // resident.setUserEmail(ReinstallApplicationClass.user.getEmail());


                            Backendless.Persistence.save(resident, new AsyncCallback<Resident>() {
                                @Override
                                public void handleResponse(Resident response) {
                                    Toast.makeText(ResidentRegister.this, "Resident added!", Toast.LENGTH_SHORT).show();

                                }

                                @Override
                                public void handleFault(BackendlessFault fault) {
                                    Toast.makeText(ResidentRegister.this, "Error: "+fault.getMessage(), Toast.LENGTH_SHORT).show();
                                    showProgress(false);
                                }
                            });


                            ResidentRegister.this.finish();
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            Toast.makeText(ResidentRegister.this, "Error: "+fault.getMessage(), Toast.LENGTH_SHORT).show();
                            showProgress(false);
                        }
                    });




                    } else {
                        Toast.makeText(ResidentRegister.this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
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