package com.example.reinstall_app.activity_classes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class LogoutDialog extends AppCompatDialogFragment {

    private LogoutDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


        builder.setTitle("LOGGING OUT!")
                .setMessage("Are you sure you want to logout?")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onLogoutClicked();
                    }
                });

            builder.create();

            return builder.create();

    }


    public interface LogoutDialogListener{

        void onLogoutClicked();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (LogoutDialogListener) context;
        }catch (ClassCastException e)
        {
            throw new ClassCastException(context.toString() + "must implement example dialog listner");

        }

    }
}
