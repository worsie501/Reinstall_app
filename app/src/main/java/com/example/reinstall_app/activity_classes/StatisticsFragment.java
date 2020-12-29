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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.example.reinstall_app.R;
import com.example.reinstall_app.app_data.City;
import com.example.reinstall_app.app_data.ReportedProblem;
import com.example.reinstall_app.app_data.Suburb;

import java.util.List;

public class StatisticsFragment extends Fragment
{

    Spinner spnrLocation;

    RecyclerView rvList;
    RecyclerView.Adapter myAdapter;
    RecyclerView.LayoutManager layoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_statistics, container, false);

        spnrLocation=v.findViewById(R.id.spnrLocation);

        rvList = v.findViewById(R.id.rvList);
        rvList.hasFixedSize();

        layoutManager = new LinearLayoutManager(getActivity());
        rvList.setLayoutManager(layoutManager);

        DataQueryBuilder spinBuilder = DataQueryBuilder.create();
        spinBuilder.setGroupBy("cityName");

        Backendless.Data.of(City.class).find(spinBuilder, new AsyncCallback<List<City>>() {
            @Override
            public void handleResponse(List<City> response) {

                ArrayAdapter<City> adapter1 = new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,response);
                adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                spnrLocation.setAdapter(adapter1);
            }

            @Override
            public void handleFault(BackendlessFault fault) {

                Toast.makeText(getActivity(), "Error: "+fault.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setGroupBy("suburbName");

        //showProgress(true);
       // tvLoad.setText("Retreiving info...please wait...");

        Backendless.Persistence.of(Suburb.class).find(queryBuilder, new AsyncCallback<List<Suburb>>() {
            @Override
            public void handleResponse(List<Suburb> response) {

                myAdapter = new HotSpotAdapter(getActivity(), response);
                rvList.setAdapter(myAdapter);
               // showProgress(false);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Toast.makeText(getActivity(), "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });





        return v;
    }
}
