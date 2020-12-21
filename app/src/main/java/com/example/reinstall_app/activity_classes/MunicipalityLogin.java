package com.example.reinstall_app.activity_classes;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.CompoundButton;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.local.UserIdStorageFactory;
import com.example.reinstall_app.app_data.Municipality;
import com.example.reinstall_app.R;
import com.example.reinstall_app.app_data.ReinstallApplicationClass;

public class MunicipalityLogin extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener{

    private View mProgressView;
    private View mLoginFormView;
    private TextView tvLoad;


    EditText etMunicipalityEmail, etMunicipalityPassword, etEmailAccount;
    Button btnMunicipalityLogin;
    TextView tvMunicipalityReset;
    SwitchCompat switchMunicipalityStayLogged=null;

    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {
        SharedPreferences settings= getSharedPreferences("status", 0);
        SharedPreferences.Editor editor=settings.edit();
        editor.putBoolean("switchStatus", isChecked);
        editor.apply();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_municipality_login);

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        tvLoad = findViewById(R.id.tvLoad);


        etMunicipalityEmail = findViewById(R.id.etMunicipalityEmail);
        etMunicipalityPassword = findViewById(R.id.etMunicipalityPassword);
        btnMunicipalityLogin = findViewById(R.id.btnMunicipalityLogin);
        tvMunicipalityReset = findViewById(R.id.tvMunicipalityReset);
        etEmailAccount=findViewById(R.id.etEmailAccount);
        switchMunicipalityStayLogged=findViewById(R.id.switchMunicipalityStayLogged);

        switchMunicipalityStayLogged.setOnCheckedChangeListener(this);

        SharedPreferences settings=getSharedPreferences("status", 0);
        boolean status=settings.getBoolean("switchStatus", false);
        switchMunicipalityStayLogged.setChecked(status);


        if(switchMunicipalityStayLogged.isChecked())
        {

            tvLoad.setText("Busy authenticating user...please wait...");
            showProgress(true);

            Backendless.UserService.isValidLogin(new AsyncCallback<Boolean>() {
                @Override
                public void handleResponse(Boolean response) {
                    if(response)
                    {
                        tvLoad.setText("User authenticated...signing in...");

                        String userObjectId= UserIdStorageFactory.instance().getStorage().get();

                        Backendless.Data.of(BackendlessUser.class).findById(userObjectId, new AsyncCallback<BackendlessUser>() {
                            @Override
                            public void handleResponse(BackendlessUser response) {

                                ReinstallApplicationClass.user=response;

                                if(ReinstallApplicationClass.user.getProperty("role").equals("Municipality")) {


                                    Intent intent = new Intent(MunicipalityLogin.this, MainActivity.class);
                                    startActivity(intent);
                                    MunicipalityLogin.this.finish();
                                }
                                else
                                {

                                    Toast.makeText(MunicipalityLogin.this, "User isn't registered as Municipality!", Toast.LENGTH_SHORT).show();

                                    Backendless.UserService.logout(new AsyncCallback<Void>() {
                                        @Override
                                        public void handleResponse(Void response) {
                                            Toast.makeText(MunicipalityLogin.this, "User signed out successfully...", Toast.LENGTH_SHORT).show();

                                            startActivity(new Intent(MunicipalityLogin.this, Login_Register_Page.class));
                                            MunicipalityLogin.this.finish();
                                        }

                                        @Override
                                        public void handleFault(BackendlessFault fault) {

                                            Toast.makeText(MunicipalityLogin.this, "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();

                                        }
                                    });

                                    showProgress(false);

                                }
                            }

                            @Override
                            public void handleFault(BackendlessFault fault) {
                                Toast.makeText(MunicipalityLogin.this, "Error: "+fault.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else
                    {
                        showProgress(false);
                    }
                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    Toast.makeText(MunicipalityLogin.this, "Error: "+fault.getMessage(), Toast.LENGTH_SHORT).show();
                    showProgress(false);

                }
            });//


        }




        tvMunicipalityReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder dialog=new AlertDialog.Builder(MunicipalityLogin.this);
                dialog.setMessage("Enter email related to acount for password reset."+"A reset link will be sent to the email address");

                View dialogView = getLayoutInflater().inflate(R.layout.dialog_view, null);
                dialog.setView(dialogView);

                etEmailAccount=dialogView.findViewById(R.id.etEmailAccount);

                dialog.setPositiveButton("RESET", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if(etEmailAccount.getText().toString().isEmpty())
                        {
                            Toast.makeText(MunicipalityLogin.this, "Please enter an email adress!", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {

                            tvLoad.setText("Busy sending reset instructions to email address...please wait...");
                            showProgress(true);

                            Backendless.UserService.restorePassword(etEmailAccount.getText().toString().trim(), new AsyncCallback<Void>() {
                                @Override
                                public void handleResponse(Void response) {
                                    showProgress(false);
                                    Toast.makeText(MunicipalityLogin.this, "Reset instructions sent to email address!", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void handleFault(BackendlessFault fault) {

                                    showProgress(false);
                                    Toast.makeText(MunicipalityLogin.this, "Error: "+fault.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            });
                        }

                    }
                });
                dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                dialog.show();

            }
        });

        btnMunicipalityLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(etMunicipalityEmail.getText().toString().isEmpty() || etMunicipalityPassword.getText().toString().isEmpty())
                {
                    Toast.makeText(MunicipalityLogin.this, "Enter all fields!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    String email = etMunicipalityEmail.getText().toString().trim();
                    String password = etMunicipalityPassword.getText().toString().trim();

                    Municipality municipality = new Municipality();
                    municipality.setEmail(email);
                    municipality.setPassword(password);

                    tvLoad.setText("Logging in...");
                    showProgress(true);


                    Backendless.UserService.login(email, password, new AsyncCallback<BackendlessUser>() {
                        @Override
                        public void handleResponse(BackendlessUser response) {

                            ReinstallApplicationClass.user=response;

                            if(ReinstallApplicationClass.user.getProperty("role").equals("Municipality")) {
                                Toast.makeText(MunicipalityLogin.this, "Successfully logged in!", Toast.LENGTH_SHORT).show();

                                 ReinstallApplicationClass.user = response;

                                Intent intent = new Intent(MunicipalityLogin.this, MainActivity.class);

                                startActivity(intent);
                                MunicipalityLogin.this.finish();
                            }
                            else
                            {
                                Toast.makeText(MunicipalityLogin.this, "User isn't registered as Municipality!", Toast.LENGTH_SHORT).show();

                                Backendless.UserService.logout(new AsyncCallback<Void>() {
                                    @Override
                                    public void handleResponse(Void response) {
                                        Toast.makeText(MunicipalityLogin.this, "User signed out successfully...", Toast.LENGTH_SHORT).show();

                                        startActivity(new Intent(MunicipalityLogin.this, RoleSelection.class));
                                        MunicipalityLogin.this.finish();
                                    }

                                    @Override
                                    public void handleFault(BackendlessFault fault) {

                                        Toast.makeText(MunicipalityLogin.this, "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();

                                    }
                                });

                                showProgress(false);
                            }
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            Toast.makeText(MunicipalityLogin.this, "Error: "+fault.getMessage(), Toast.LENGTH_SHORT).show();
                            showProgress(false);
                        }
                    }, true);
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