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
import com.backendless.persistence.DataQueryBuilder;
import com.example.reinstall_app.R;
import com.example.reinstall_app.app_data.ProblemType;

import java.util.List;

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


              DataQueryBuilder queryBuilder = DataQueryBuilder.create();
              queryBuilder.setPageSize(80);


                Backendless.Persistence.of(ProblemType.class).find(queryBuilder, new AsyncCallback<List<ProblemType>>() {
                    @Override
                    public void handleResponse(List<ProblemType> response) {

                        boolean bFound = false;

                        if(etNewCategory.getText().toString().isEmpty() | etNewDesc.getText().toString().isEmpty())
                        {
                            Toast.makeText(getContext(), "Please enter all fields", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {

                            ProblemType problemType = new ProblemType();
                            problemType.setProblemName(etNewCategory.getText().toString().trim());
                            problemType.setProblemDescription(etNewDesc.getText().toString().trim());

                            for(int i = 0; i < response.size(); i++)
                            {

                                if (etNewCategory.getText().toString().trim().equals(response.get(i).getProblemName()))
                                {

                                    bFound = true;
                                    i = response.size();
                                    Toast.makeText(getContext(), "Category already exists", Toast.LENGTH_SHORT).show();

                                }
                                else
                                {
                                    bFound = false;
                                }

                            }

                            if(bFound == false)
                            {
                                Backendless.Persistence.save(problemType, new AsyncCallback<ProblemType>() {
                                    @Override
                                    public void handleResponse(ProblemType response) {

                                        etNewCategory.setText("");
                                        etNewDesc.setText("");
                                        Toast.makeText(getActivity(), "Category successfully added!", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void handleFault(BackendlessFault fault) {
                                        Toast.makeText(getActivity(), "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }


                        }

                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        Toast.makeText(getContext(), "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });




            }
        });

    }
}
