package com.example.reinstall_app.activity_classes;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.local.UserIdStorageFactory;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.example.reinstall_app.R;
import com.example.reinstall_app.app_data.ReinstallApplicationClass;
import com.example.reinstall_app.app_data.Resident;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

import static com.example.reinstall_app.app_data.ReinstallApplicationClass.AUTH_KEY;
import static com.example.reinstall_app.app_data.ReinstallApplicationClass.user;

public class CombinedLogin extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener{

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +         //at least 1 digit
                    "(?=.*[a-z])" +         //at least 1 lower case letter
                    "(?=.*[A-Z])" +         //at least 1 upper case letter
                   // "(?=.*[a-zA-Z])" +      //any letter
                    "(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{4,}" +               //at least 4 characters
                    "$");

    TextInputLayout textInputEmail, textinputPassword;
    TextInputEditText etPassword, etEmail;
    Button btnLogin, btnRegister;
    Switch swStayLoggedin;
    TextView tvResetPassword;
    EditText etEmailAccount;

    private View mProgressView;
    private View mLoginFormView;
    private TextView tvLoad;


    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {
        SharedPreferences settings = getSharedPreferences("status", 0);
        SharedPreferences.Editor editor=settings.edit();
        editor.putBoolean("switchStatus", isChecked);
        editor.apply();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combined_login);


        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        tvLoad = findViewById(R.id.tvLoad);

        textInputEmail = findViewById(R.id.text_input_email);
        textinputPassword = findViewById(R.id.text_input_password);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        swStayLoggedin = findViewById(R.id.swStayLoggedIn);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        tvResetPassword = findViewById(R.id.tvResetPassword);


        this.setTitle("Login");

        swStayLoggedin.setOnCheckedChangeListener(this);


        SharedPreferences settings = getSharedPreferences("status",0);
        boolean status = settings.getBoolean("switchStatus", false);
        swStayLoggedin.setChecked(status);


        if(swStayLoggedin.isChecked())
        {
            tvLoad.setText("Busy authenticating user...please wait...");
            showProgress(true);

            Backendless.UserService.isValidLogin(new AsyncCallback<Boolean>() {
                @Override
                public void handleResponse(Boolean response) {

                    if(response) {
                        tvLoad.setText("User authenticating...signing in...");

                        String userObjectId = UserIdStorageFactory.instance().getStorage().get();

                        Backendless.Data.of(BackendlessUser.class).findById(userObjectId, new AsyncCallback<BackendlessUser>() {
                            @Override
                            public void handleResponse(BackendlessUser response) {

                                ReinstallApplicationClass.user = response;

                                if(ReinstallApplicationClass.user.getProperty("role").equals("Resident")) {

                                    String email = user.getEmail();

                                    String EMAIl_PATTERN = "[^a-zA-Z0-9!#$%&'*+-/=?^_`{|}~]+";
                                    String UID =   email.replaceAll(EMAIl_PATTERN, "").replaceAll("\\p{Punct}", "");


                                    if (CometChat.getLoggedInUser() == null) {
                                        CometChat.login(UID, AUTH_KEY, new CometChat.CallbackListener<User>() {

                                            @Override
                                            public void onSuccess(User user) {
                                                // Toast.makeText(ResidentLogin.this, "Comet chat user Successfully logged in!", Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void onError(CometChatException e) {
                                                Toast.makeText(CombinedLogin.this, "Error" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } else {
                                        // User already logged in
                                        // Toast.makeText(ResidentLogin.this, "User already logged in ", Toast.LENGTH_SHORT).show();
                                    }


                                }
                                else
                                {
                                    Toast.makeText(CombinedLogin.this, "Logged in as Municipality", Toast.LENGTH_SHORT).show();
                                }


                                Intent intent = new Intent(CombinedLogin.this, MainActivity.class);
                                startActivity(intent);
                                CombinedLogin.this.finish();

                            }

                            @Override
                            public void handleFault(BackendlessFault fault) {

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

                }
            });

        }


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validateEmail())
                {

                    String email = textInputEmail.getEditText().getText().toString().trim();
                    String password = textinputPassword.getEditText().getText().toString().trim();

                    String EMAIl_PATTERN = "[^a-zA-Z0-9!#$%&'*+-/=?^_`{|}~]+";
                    String modifiedEmail = email.replaceAll(EMAIl_PATTERN, "").replaceAll("\\p{Punct}", "");
                    String UID = modifiedEmail;


                    tvLoad.setText("Authenticating...please wait...");
                    showProgress(true);

                    Backendless.UserService.login(email, password, new AsyncCallback<BackendlessUser>() {
                        @Override
                        public void handleResponse(BackendlessUser response) {

                            ReinstallApplicationClass.user = response;

                            tvLoad.setText("User authenticated...Logging in...");
                            if(response.getProperty("role").equals("Resident"))
                            {
                                if (CometChat.getLoggedInUser() == null) {
                                    CometChat.login(UID, AUTH_KEY, new CometChat.CallbackListener<User>() {

                                        @Override
                                        public void onSuccess(User user) {
                                            // Toast.makeText(ResidentLogin.this, "Comet chat user Successfully logged in!", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onError(CometChatException e) {
                                            Toast.makeText(CombinedLogin.this, "Error" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else
                                {
                                    // User already logged in
                                    //Toast.makeText(ResidentLogin.this, "User already logged in ", Toast.LENGTH_SHORT).show();
                                }

                            }
                            else
                            {
                                Toast.makeText(CombinedLogin.this, "Logged in as Municipality", Toast.LENGTH_SHORT).show();
                            }

                                Intent intent = new Intent(CombinedLogin.this, MainActivity.class);
                                startActivity(intent);

                                CombinedLogin.this.finish();
                                showProgress(false);

                            Toast.makeText(CombinedLogin.this, "Successfully logged in!", Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {

                            Toast.makeText(CombinedLogin.this, "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();
                            showProgress(false);
                        }
                    }, true);
                }
            }
        });


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(CombinedLogin.this, CombinedRegister.class);
                startActivity(intent);

            }
        });


        tvResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder dialog=new AlertDialog.Builder(CombinedLogin.this);
                dialog.setMessage("Enter email related to acount for password reset."+"A reset link will be sent to the email address");

                View dialogView = getLayoutInflater().inflate(R.layout.dialog_view, null);
                dialog.setView(dialogView);

                etEmailAccount = dialogView.findViewById(R.id.etEmailAccount);

                dialog.setPositiveButton("RESET", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if(etEmailAccount.getText().toString().isEmpty())
                        {
                            Toast.makeText(CombinedLogin.this, "Please enter an email address!", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {

                            tvLoad.setText("Sending reset instructions to email address...please wait...");
                            showProgress(true);

                            Backendless.UserService.restorePassword(etEmailAccount.getText().toString().trim(), new AsyncCallback<Void>() {
                                @Override
                                public void handleResponse(Void response) {
                                    showProgress(false);
                                    Toast.makeText(CombinedLogin.this, "Reset instructions sent to email address!", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void handleFault(BackendlessFault fault) {

                                    showProgress(false);
                                    Toast.makeText(CombinedLogin.this, "Error: "+fault.getMessage(), Toast.LENGTH_SHORT).show();

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

    }




    private boolean validateEmail(){
        String emailInput = textInputEmail.getEditText().getText().toString().trim();

        if(emailInput.isEmpty())
        {
            textInputEmail.setError("Field cant be empty");
            return false;
        }else if(!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches())
        {

            textInputEmail.setError("Invalid email address");
            return false;

        }
        else
        {
            textInputEmail.setError("");
            textInputEmail.setErrorEnabled(false);
            return true;
        }

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