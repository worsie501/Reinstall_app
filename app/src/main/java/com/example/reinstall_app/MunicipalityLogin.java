package com.example.reinstall_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MunicipalityLogin extends AppCompatActivity {

    EditText etMunicipalityEmail;
    EditText etMunicipalityPassword;
    Button btnMunicipalityLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_municipality_login);

        etMunicipalityEmail=findViewById(R.id.etMunicipalityEmail);
        etMunicipalityPassword=findViewById(R.id.etMunicipalityPassword);
        btnMunicipalityLogin=findViewById(R.id.btnMunicipalityLogin);

        btnMunicipalityLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }
}