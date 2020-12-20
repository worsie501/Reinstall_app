package com.example.reinstall_app.activity_classes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.reinstall_app.R;

public class RoleSelection extends AppCompatActivity {

    Button btnAdmin;
    Button btnMunicipality;
    Button btnResident;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_selection);

        btnAdmin=findViewById(R.id.btnAdmin);
        btnMunicipality=findViewById(R.id.btnMainRegister);
        btnResident=findViewById(R.id.btnResident);

        btnAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                 String verify="Admin";

                Intent admin=new Intent(RoleSelection.this,  Login_Register_Page.class );

                admin.putExtra("verifyData", verify.trim());

               startActivity(admin);

            }
        });

        btnMunicipality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String verify="Municipality";

                Intent municipality=new Intent(RoleSelection.this,  Login_Register_Page.class );

                municipality.putExtra("verifyData", verify.trim());

               startActivity(municipality);

            }
        });

        btnResident.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String verify="Resident";

                Intent resident=new Intent(RoleSelection.this,  Login_Register_Page.class );

               resident.putExtra("verifyData",verify.trim());

               startActivity(resident);

            }
        });

    }
}