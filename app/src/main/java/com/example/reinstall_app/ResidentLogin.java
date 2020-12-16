package com.example.reinstall_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ResidentLogin extends AppCompatActivity {

    EditText etResidentEmail;
    EditText etResidentPassword;
    Button btnResidentLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resident__login);

        etResidentEmail=findViewById(R.id.etResidentEmail);
        etResidentPassword=findViewById(R.id.etResidentPassword);
        btnResidentLogin=findViewById(R.id.btnResidentLogin);

        btnResidentLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


    }
}