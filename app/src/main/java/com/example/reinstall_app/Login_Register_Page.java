package com.example.reinstall_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Login_Register_Page extends AppCompatActivity {

    Button btnMainLogin;
    Button btnMainRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login__register__page);

        final String verify=getIntent().getStringExtra("verifyData");


        btnMainLogin=findViewById(R.id.btnMainLogin);
        btnMainRegister=findViewById(R.id.btnMainRegister);

        btnMainLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(verify.equals("Resident"))
                {
                    Intent intent = new Intent(Login_Register_Page.this, com.example.reinstall_app.ResidentLogin.class);

                    startActivity(intent);
                }
                else if(verify.equals("Admin"))
                {
                    Intent intent = new Intent(Login_Register_Page.this, com.example.reinstall_app.AdminLogin.class);

                    startActivity(intent);
                }
                else
                {
                    Intent intent = new Intent(Login_Register_Page.this, com.example.reinstall_app.MunicipalityLogin.class);

                    startActivity(intent);
                }

            }
        });

        btnMainRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(verify.equals("Resident"))
                {
                    Intent intent = new Intent(Login_Register_Page.this, com.example.reinstall_app.ResidentRegister.class);

                    startActivity(intent);
                }
                else if(verify.equals("Admin"))
                {
                    Intent intent = new Intent(Login_Register_Page.this, com.example.reinstall_app.AdminRegister.class);

                    startActivity(intent);
                }
                else
                {
                    Intent intent = new Intent(Login_Register_Page.this, com.example.reinstall_app.MunicipalityRegister.class);

                    startActivity(intent);
                }

            }
        });

    }
}