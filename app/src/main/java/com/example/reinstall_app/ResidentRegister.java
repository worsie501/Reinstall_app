package com.example.reinstall_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class ResidentRegister extends AppCompatActivity {

    EditText etResidentName;
    EditText etResidentRegisterEmail;
    EditText etResidentRegisterPasword;
    Spinner spnrResidentLocations;
    Button btnResidentRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resident_register);

        etResidentName=findViewById(R.id.etResidentName);
        etResidentRegisterEmail=findViewById(R.id.etResidentRegisterEmail);
        etResidentRegisterPasword=findViewById(R.id.etResidentRegisterPasword);
        spnrResidentLocations=findViewById(R.id.spnrResidentLocations);
        btnResidentRegister=findViewById(R.id.btnResidentRegister);

        btnResidentRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}