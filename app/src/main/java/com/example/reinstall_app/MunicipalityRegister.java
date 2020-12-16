package com.example.reinstall_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class MunicipalityRegister extends AppCompatActivity {

    EditText etMunicipalityName;
    EditText etMunicipalityRegisterEmail;
    EditText etMunicipalityRegisterPassword;
    Spinner spnrMunicipalityLocation;
    Button btnMunicipalityRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_municipality_register);

        etMunicipalityName=findViewById(R.id.etMunicipalityName);
        etMunicipalityRegisterEmail=findViewById(R.id.etMunicipalityRegisterEmail);
        etMunicipalityRegisterPassword=findViewById(R.id.etMunicipalityRegisterPassword);
        spnrMunicipalityLocation=findViewById(R.id.spnrMunicipalityLocation);
        btnMunicipalityRegister=findViewById(R.id.btnMunicipalityRegister);

        btnMunicipalityRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}