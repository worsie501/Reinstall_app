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

public class AdminRegister extends AppCompatActivity {

    private View mProgressView;
    private View mLoginFormView;
    private TextView tvLoad;

    EditText etAdminName, etAdminRegisterEmail, etAdminRegisterPassword, etConfirmAdminPassword;

    Button btnAdminRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_register);

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        tvLoad = findViewById(R.id.tvLoad);

        etAdminName=findViewById(R.id.etAdminName);
        etAdminRegisterEmail=findViewById(R.id.etAdminRegisterEmail);
        etAdminRegisterPassword=findViewById(R.id.etAdminRegisterPassword);
        etConfirmAdminPassword=findViewById(R.id.etConfirmAdminPassword);
        btnAdminRegister=findViewById(R.id.btnAdminRegister);

        btnAdminRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(etAdminName.getText().toString().isEmpty()||etAdminRegisterEmail.getText().toString().isEmpty()||etAdminRegisterPassword.getText().toString().isEmpty())
                {
                    Toast.makeText(AdminRegister.this, "Please enter all fields!", Toast.LENGTH_SHORT).show();
                }
                else {

                    if (etAdminRegisterPassword.getText().toString().trim().equals(etConfirmAdminPassword.getText().toString().trim())) {


                        BackendlessUser user = new BackendlessUser();
                        user.setEmail(etAdminRegisterEmail.getText().toString().trim());
                        user.setPassword(etAdminRegisterPassword.getText().toString().trim());
                        user.setProperty("name", etAdminName.getText().toString().trim());

                        tvLoad.setText("Registering...Please wait...");
                        showProgress(true);

                        Backendless.UserService.register(user, new AsyncCallback<BackendlessUser>() {
                            @Override
                            public void handleResponse(BackendlessUser response) {
                                Toast.makeText(AdminRegister.this, "Admin Registered", Toast.LENGTH_SHORT).show();

                                Admin admin=new Admin();
                                admin.setAdminEmail(etAdminRegisterEmail.getText().toString().trim());
                                admin.setAdminPassword(etAdminRegisterPassword.getText().toString().trim());
                                admin.setAdminName(etAdminName.getText().toString().trim());

                                Backendless.Persistence.save(admin, new AsyncCallback<Admin>() {
                                    @Override
                                    public void handleResponse(Admin response) {
                                        Toast.makeText(AdminRegister.this, "Admin Added", Toast.LENGTH_SHORT).show();
                                    }
                                    @Override
                                    public void handleFault(BackendlessFault fault) {
                                        Toast.makeText(AdminRegister.this, "Error: "+fault.getMessage(), Toast.LENGTH_SHORT).show();
                                        showProgress(false);
                                    }
                                });

                                AdminRegister.this.finish();
                            }

                            @Override
                            public void handleFault(BackendlessFault fault) {
                                Toast.makeText(AdminRegister.this, "Error: "+fault.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                    else {
                        Toast.makeText(AdminRegister.this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
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