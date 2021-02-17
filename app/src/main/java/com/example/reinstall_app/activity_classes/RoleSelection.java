package com.example.reinstall_app.activity_classes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.reinstall_app.R;

public class RoleSelection extends AppCompatActivity {

    Button btnAdmin;
    Button btnMunicipality;
    Button btnResident;
    int autoroleSelected;
    int verifyRoleSelected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_selection);

        SharedPreferences sharedPreferences=getSharedPreferences("autoRoleSelect", MODE_PRIVATE);

        verifyRoleSelected = sharedPreferences.getInt("roleSelected", autoroleSelected);

        final int intentAutoRoleSelect=getIntent().getIntExtra("autoRoleSelect",3);

        if(intentAutoRoleSelect==4)
        {
            verifyRoleSelected=intentAutoRoleSelect;

        }

        btnAdmin=findViewById(R.id.btnAdmin);
        btnMunicipality=findViewById(R.id.btnMainRegister);
        btnResident=findViewById(R.id.btnResident);

        if(verifyRoleSelected==0)
        {

            String verify="Admin";

            Intent admin=new Intent(RoleSelection.this,  Login_Register_Page.class );

            admin.putExtra("verifyData", verify.trim());

            startActivity(admin);

        }
        else if(verifyRoleSelected==1)
        {

            String verify="Municipality";

            Intent municipality=new Intent(RoleSelection.this,  Login_Register_Page.class );

            municipality.putExtra("verifyData", verify.trim());

            startActivity(municipality);

        }
        else if(verifyRoleSelected==2)
        {
            String verify="Resident";

            Intent resident=new Intent(RoleSelection.this,  Login_Register_Page.class );

            resident.putExtra("verifyData",verify.trim());

            startActivity(resident);

        }
        else {

            btnAdmin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    autoroleSelected=0;

                    SharedPreferences preferences=getSharedPreferences("autoRoleSelect", MODE_PRIVATE);
                    SharedPreferences.Editor editor=preferences.edit();
                    editor.putInt("roleSelected", autoroleSelected);
                    editor.apply();

                    String verify = "Admin";

                    Intent admin = new Intent(RoleSelection.this, Login_Register_Page.class);

                    admin.putExtra("verifyData", verify.trim());

                    startActivity(admin);

                }
            });

            btnMunicipality.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    autoroleSelected=1;

                    SharedPreferences preferences=getSharedPreferences("autoRoleSelect", MODE_PRIVATE);
                    SharedPreferences.Editor editor=preferences.edit();
                    editor.putInt("roleSelected", autoroleSelected);
                    editor.apply();

                    String verify = "Municipality";

                    Intent municipality = new Intent(RoleSelection.this, Login_Register_Page.class);

                    municipality.putExtra("verifyData", verify.trim());

                    startActivity(municipality);

                }
            });

            btnResident.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    autoroleSelected=2;

                    SharedPreferences preferences=getSharedPreferences("autoRoleSelect", MODE_PRIVATE);
                    SharedPreferences.Editor editor=preferences.edit();
                    editor.putInt("roleSelected", autoroleSelected);
                    editor.apply();

                    String verify = "Resident";

                    Intent resident = new Intent(RoleSelection.this, Login_Register_Page.class);

                    resident.putExtra("verifyData", verify.trim());

                    startActivity(resident);

                }
            });
        }

    }
}