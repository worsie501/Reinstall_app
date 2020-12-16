package com.example.reinstall_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class AdminRegister extends AppCompatActivity {

    EditText etAdminName;
    EditText etAdminRegisterEmail;
    EditText etAdminRegisterPassword;
    Spinner spnrAdminLocation;
    Button btnAdminRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_register);

        etAdminName=findViewById(R.id.etAdminName);
        etAdminRegisterEmail=findViewById(R.id.etAdminRegisterEmail);
        etAdminRegisterPassword=findViewById(R.id.etAdminRegisterPassword);
        spnrAdminLocation=findViewById(R.id.spnrAdminLocation);
        btnAdminRegister=findViewById(R.id.btnAdminRegister);

        btnAdminRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}