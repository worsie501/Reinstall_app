package com.example.reinstall_app.activity_classes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.example.reinstall_app.R;
import com.example.reinstall_app.app_data.ProblemType;

public class CategoryFragment extends Fragment
{

    Button btnCategoryAdd;
    EditText etNewCategory, etNewDesc;
    View v;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_categories, container, false);

        return v;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        btnCategoryAdd = v.findViewById(R.id.btnCategoryAdd);
        etNewCategory = v.findViewById(R.id.etNewCategory);
        etNewDesc = v.findViewById(R.id.etNewDesc);

        btnCategoryAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ProblemType problemType = new ProblemType();
                problemType.setProblemName(etNewCategory.getText().toString().trim());
                problemType.setProblemDescription(etNewDesc.getText().toString().trim());

                Backendless.Persistence.save(problemType, new AsyncCallback<ProblemType>() {
                    @Override
                    public void handleResponse(ProblemType response) {
                        Toast.makeText(getActivity(), "Category successfully added!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        Toast.makeText(getActivity(), "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }
}
