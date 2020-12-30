package com.example.reinstall_app.activity_classes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.reinstall_app.app_data.ReportedProblem;

import java.util.List;

public class FeedFragment extends Fragment implements FeedAdapter.FeedItemClicked
{

    RecyclerView rvFeed;
    RecyclerView.Adapter feedAdapter;
    RecyclerView.LayoutManager layoutManager;
    View v;

    public FeedFragment(){}


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

         v = inflater.inflate(R.layout.fragment_feed, container, false);




        return v;
    }

    @Override
    public void onItemClicked(int index) {

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        rvFeed = v.findViewById(R.id.rvFeed);
        rvFeed.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this.getActivity());
        rvFeed.setLayoutManager(layoutManager);


        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setGroupBy("userName");

        Backendless.Persistence.of(ReportedProblem.class).find(queryBuilder, new AsyncCallback<List<ReportedProblem>>() {
            @Override
            public void handleResponse(List<ReportedProblem> response) {

                feedAdapter = new FeedAdapter(getActivity(), response);
                rvFeed.setAdapter(feedAdapter);

                Toast.makeText(getActivity(), "Eish", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void handleFault(BackendlessFault fault) {

                Toast.makeText(getActivity(), "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });





    }
}
