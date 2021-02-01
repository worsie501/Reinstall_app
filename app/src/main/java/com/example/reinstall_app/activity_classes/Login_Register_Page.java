package com.example.reinstall_app.activity_classes;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.reinstall_app.R;

public class Login_Register_Page extends AppCompatActivity {
//123
    private View mProgressView;
    private View mLoginFormView;
    private TextView tvLoad;

    Button btnMainLogin;
    Button btnMainRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login__register__page);

        final String verify=getIntent().getStringExtra("verifyData");

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        tvLoad = findViewById(R.id.tvLoad);


        btnMainLogin=findViewById(R.id.btnMainLogin);
        btnMainRegister=findViewById(R.id.btnMainRegister);

        btnMainLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(verify.equals("Resident"))
                {
                    Intent intent = new Intent(Login_Register_Page.this, ResidentLogin.class);

                    startActivity(intent);
                }
                else if(verify.equals("Admin"))
                {

                    Intent intent = new Intent(Login_Register_Page.this, AdminLogin.class);

                    startActivity(intent);
                }
                else
                {
                    Intent intent = new Intent(Login_Register_Page.this, MunicipalityLogin.class);

                    startActivity(intent);
                }

            }
        });

        btnMainRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(verify.equals("Resident"))
                {
                    Intent intent = new Intent(Login_Register_Page.this, ResidentRegister.class);

                    startActivity(intent);
                }
                else if(verify.equals("Admin"))
                {
                    Intent intent = new Intent(Login_Register_Page.this, AdminRegister.class);

                    startActivity(intent);
                }
                else
                {
                    Intent intent = new Intent(Login_Register_Page.this, MunicipalityRegister.class);

                    startActivity(intent);
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