package com.example.reinstall_app.activity_classes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.example.reinstall_app.R;
import com.example.reinstall_app.app_data.District;
import com.example.reinstall_app.app_data.ProblemType;

import java.util.List;

public class ReportFragment extends Fragment
{


    Spinner spCategory;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_report, container, false);

        spCategory =  v.findViewById(R.id.spCategory);

        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setGroupBy("problemName");

        Backendless.Data.of(ProblemType.class).find(queryBuilder, new AsyncCallback<List<ProblemType>>() {
            @Override
            public void handleResponse(List<ProblemType> response) {

                ArrayAdapter<ProblemType> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, response);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                spCategory.setAdapter(adapter);
            }

            @Override
            public void handleFault(BackendlessFault fault) {

                Toast.makeText(getActivity(), "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }
}
