package com.example.reinstall_app.activity_classes;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.example.reinstall_app.R;
import com.example.reinstall_app.app_data.City;
import com.example.reinstall_app.app_data.Resident;
import com.example.reinstall_app.app_data.Suburb;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;
import java.util.regex.Pattern;

import static com.example.reinstall_app.app_data.ReinstallApplicationClass.AUTH_KEY;

public class CombinedRegister extends AppCompatActivity {

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


    TextInputLayout textInputName, textInputEmail, textInputRegisterPass, textInputRegisterConfirm ;
    Button btnRregister;
    Spinner spCity, spSuburb;


    private View mProgressView;
    private View mLoginFormView;
    private TextView tvLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combined_register);

        String role = "Resident";

        this.setTitle("Register");

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        tvLoad = findViewById(R.id.tvLoad);


        textInputName = findViewById(R.id.text_Input_Name);
        textInputEmail = findViewById(R.id.text_input_email);
        textInputRegisterPass = findViewById(R.id.text_register_pass);
        textInputRegisterConfirm = findViewById(R.id.text_input_register_confirm);

        btnRregister = findViewById(R.id.btnRregister);

        spCity = findViewById(R.id.spCity);
        spSuburb = findViewById(R.id.spSuburb);


        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setGroupBy("cityName");

        Backendless.Data.of(City.class).find(queryBuilder, new AsyncCallback<List<City>>() {
            @Override
            public void handleResponse(List<City> response) {

                ArrayAdapter<City> adapter1 = new ArrayAdapter<>(CombinedRegister.this,android.R.layout.simple_list_item_1,response);
                adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                spCity.setAdapter(adapter1);
            }

            @Override
            public void handleFault(BackendlessFault fault) {

                Toast.makeText(CombinedRegister.this, "Error: "+fault.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });


        int PAGESIZE = 80;
        DataQueryBuilder qryBuilder = DataQueryBuilder.create();
        qryBuilder.setGroupBy("suburbName");
        qryBuilder.setPageSize(PAGESIZE);

        Backendless.Data.of(Suburb.class).find(qryBuilder, new AsyncCallback<List<Suburb>>() {
            @Override
            public void handleResponse(List<Suburb> response) {

                ArrayAdapter<Suburb> adapter2 = new ArrayAdapter<>(CombinedRegister.this,android.R.layout.simple_list_item_1,response);
                adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                spSuburb.setAdapter(adapter2);
            }

            @Override
            public void handleFault(BackendlessFault fault) {

                Toast.makeText(CombinedRegister.this, "Error: "+fault.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });


        btnRregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               /* ValidatePassword();
                ValidateConfirm();
                ValidatePasswordMatch();
                ValidateEmail();*/

                if (textInputName.getEditText().getText().toString().isEmpty()) {

                    textInputName.setError("Please enter your name");

                } else
                    {

                        textInputName.setError("");
                        textInputName.setErrorEnabled(false);

                    if (ValidatePassword() & ValidateEmail() & ValidateConfirm() & ValidatePasswordMatch()) {

                        BackendlessUser user = new BackendlessUser();
                        user.setEmail(textInputEmail.getEditText().getText().toString().trim());
                        user.setPassword(textInputRegisterPass.getEditText().getText().toString().trim());
                        user.setProperty("name", textInputName.getEditText().getText().toString().trim());
                        user.setProperty("role", role);


                        tvLoad.setText("Registering...Please wait...");
                        showProgress(true);


                        //Register Comet chat User
                        //-------------------------------------------------------------------------
                        String email = textInputEmail.getEditText().getText().toString().trim();
                        String EMAIl_PATTERN = "[^a-zA-Z0-9!#$%&'*+-/=?^_`{|}~]+";
                        String modifiedEmail = email.replaceAll(EMAIl_PATTERN, "").replaceAll("\\p{Punct}", "");


                        User chatUser = new User();
                        chatUser.setUid(modifiedEmail); // Replace with the UID for the user to be created
                        chatUser.setName(textInputName.getEditText().getText().toString().trim()); // Replace with the name of the user

                        CometChat.createUser(chatUser, AUTH_KEY, new CometChat.CallbackListener<User>() {
                            @Override
                            public void onSuccess(User user) {
                                // Toast.makeText(ResidentRegister.this, "Comet chat Resident Registered!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(CometChatException e) {
                                Toast.makeText(CombinedRegister.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });


                        //Register Backendless user
                        //-------------------------------------------------------------------------
                        Backendless.UserService.register(user, new AsyncCallback<BackendlessUser>() {
                            @Override
                            public void handleResponse(BackendlessUser response) {

                                tvLoad.setText("Registered");
                                Toast.makeText(CombinedRegister.this, "Resident Registered!", Toast.LENGTH_SHORT).show();

                                Resident resident = new Resident();

                                resident.setResidentName(textInputName.getEditText().getText().toString().trim());
                                resident.setEmail(textInputEmail.getEditText().getText().toString().trim());
                                resident.setCity(spCity.getSelectedItem().toString().trim());
                                resident.setSuburb(spSuburb.getSelectedItem().toString().trim());
                                resident.setUserPassword(textInputRegisterPass.getEditText().getText().toString().trim());

                                Backendless.Persistence.save(resident, new AsyncCallback<Resident>() {
                                    @Override
                                    public void handleResponse(Resident response) {
                                        //Toast.makeText(CombinedRegister.this, "Resident added!", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void handleFault(BackendlessFault fault) {
                                        Toast.makeText(CombinedRegister.this, "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();
                                        showProgress(false);
                                    }
                                });


                                CombinedRegister.this.finish();

                                Toast.makeText(CombinedRegister.this, "Check your email to confirm your registration", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void handleFault(BackendlessFault fault) {
                                Toast.makeText(CombinedRegister.this, "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();
                                showProgress(false);
                            }
                        });


                    }

                }
            }
        });

    }


    private boolean ValidatePasswordMatch()
    {
        if(textInputRegisterPass.getEditText().getText().toString().trim().isEmpty() | textInputRegisterConfirm.getEditText().getText().toString().trim().isEmpty() )
        {
            textInputRegisterPass.setError("Field cant be empty");
            textInputRegisterConfirm.setError("Field cant be empty");
            return false;

        }
        else if(!textInputRegisterPass.getEditText().getText().toString().trim().equals(textInputRegisterConfirm.getEditText().getText().toString().trim()))
        {
            textInputRegisterPass.setError("Passwords does not match");
            textInputRegisterConfirm.setError("Passwords does not match");
            return false;
        }
        else if(ValidatePassword() & ValidateConfirm())
        {

            textInputRegisterPass.setError("");
            textInputRegisterConfirm.setError("");

            textInputRegisterPass.setErrorEnabled(false);
            textInputRegisterConfirm.setErrorEnabled(false);

            return true;
        }
        else
        {
            return false;
        }


    }

    private boolean ValidatePassword(){

        String passwordInput = textInputRegisterPass.getEditText().getText().toString().trim();

            if(passwordInput.isEmpty())
            {
                textInputRegisterPass.setError("Field cant be empty");
                return false;

            }else if (!PASSWORD_PATTERN.matcher(passwordInput).matches())
            {
                textInputRegisterPass.setError("Password to weak");
                return false;
            }
            else
            {
                textInputRegisterConfirm.setError("");
                textInputRegisterConfirm.setErrorEnabled(false);
                return true;
            }

    }


    private boolean ValidateConfirm()
    {

        String passwordConfirm = textInputRegisterConfirm.getEditText().getText().toString().trim();

        if(passwordConfirm.isEmpty())
        {
            textInputRegisterConfirm.setError("Field cant be empty");
            return false;
        }else if(!PASSWORD_PATTERN.matcher(passwordConfirm).matches())
        {

            textInputRegisterConfirm.setError("Password to weak");
            return false;
        }
        else
        {
            textInputRegisterPass.setError("");
            textInputRegisterPass.setErrorEnabled(false);
            return true;
        }

    }


    private boolean ValidateEmail(){
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