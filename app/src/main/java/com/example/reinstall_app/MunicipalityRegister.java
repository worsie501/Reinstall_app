package com.example.reinstall_app;

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

public class MunicipalityRegister extends AppCompatActivity {

    private View mProgressView;
    private View mLoginFormView;
    private TextView tvLoad;

    EditText etMunicipalityName, etMunicipalityRegisterEmail, etMunicipalityRegisterPassword, etConfirmMunicipalityPassword;
    Spinner spnrMunicipalityLocation;
    Button btnMunicipalityRegister;
    String role="Municipality";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_municipality_register);

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        tvLoad = findViewById(R.id.tvLoad);

        etMunicipalityName=findViewById(R.id.etMunicipalityName);
        etMunicipalityRegisterEmail=findViewById(R.id.etMunicipalityRegisterEmail);
        etMunicipalityRegisterPassword=findViewById(R.id.etMunicipalityRegisterPassword);
        etConfirmMunicipalityPassword=findViewById(R.id.etConfirmMunicipalityPassword);
        spnrMunicipalityLocation=findViewById(R.id.spnrMunicipalityLocation);
        btnMunicipalityRegister=findViewById(R.id.btnMunicipalityRegister);

        btnMunicipalityRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(etMunicipalityName.getText().toString().isEmpty()||etMunicipalityRegisterEmail.getText().toString().isEmpty()||etMunicipalityRegisterPassword.getText().toString().isEmpty())
                {
                    Toast.makeText(MunicipalityRegister.this, "Please enter all fields!", Toast.LENGTH_SHORT).show();

                }

                else {

                    if (etMunicipalityRegisterPassword.getText().toString().trim().equals(etConfirmMunicipalityPassword.getText().toString().trim())) {
                        Municipality municipality=new Municipality();
                        municipality.setEmail(etMunicipalityRegisterEmail.getText().toString().trim());
                        municipality.setPassword(etMunicipalityRegisterPassword.getText().toString().trim());
                        municipality.setMunicipalityName(etMunicipalityName.getText().toString().trim());

                        tvLoad.setText("Registering...Please wait...");
                        showProgress(true);

                        Backendless.Persistence.save(municipality, new AsyncCallback<Municipality>() {
                            @Override
                            public void handleResponse(Municipality response) {
                                Toast.makeText(MunicipalityRegister.this, "Municipality Registered!", Toast.LENGTH_SHORT).show();
                                etMunicipalityName.setText(null);
                                etMunicipalityRegisterEmail.setText(null);
                                etConfirmMunicipalityPassword.setText(null);
                                etMunicipalityRegisterPassword.setText(null);
                                showProgress(false);
                            }

                            @Override
                            public void handleFault(BackendlessFault fault) {
                                Toast.makeText(MunicipalityRegister.this, "Error: "+fault.getMessage(), Toast.LENGTH_SHORT).show();
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