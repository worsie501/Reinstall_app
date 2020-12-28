package com.example.reinstall_app.activity_classes;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.example.reinstall_app.R;
import com.example.reinstall_app.app_data.District;
import com.example.reinstall_app.app_data.ProblemType;
import com.example.reinstall_app.app_data.ReportedProblem;
import com.example.reinstall_app.app_data.Suburb;
import com.example.reinstall_app.app_data.User;

import java.util.List;

import static android.content.Context.RECEIVER_VISIBLE_TO_INSTANT_APPS;
import static com.example.reinstall_app.app_data.ReinstallApplicationClass.user;

public class ReportFragment extends Fragment
{


    Spinner spCategory, spnrLocationSelect;
    Button btnSubmitReport;
    EditText etDescription;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_report, container, false);

        spCategory =  v.findViewById(R.id.spCategory);
        spnrLocationSelect=v.findViewById(R.id.spnrLocationSelect);
        btnSubmitReport=v.findViewById(R.id.btnSubmitReport);
        etDescription=v.findViewById(R.id.etDescription);

        DataQueryBuilder queryBuilder1 = DataQueryBuilder.create();
        queryBuilder1.setGroupBy("suburbName");

        Backendless.Data.of(Suburb.class).find(queryBuilder1, new AsyncCallback<List<Suburb>>() {
            @Override
            public void handleResponse(List<Suburb> response) {

                ArrayAdapter<Suburb> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, response);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                spnrLocationSelect.setAdapter(adapter);
            }

            @Override
            public void handleFault(BackendlessFault fault) {

            }
        });

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

        btnSubmitReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (etDescription.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "Please enter a short description of the problem", Toast.LENGTH_SHORT).show();
                } else {

                    final ReportedProblem problem = new ReportedProblem();
                    problem.setProblemType(spCategory.getSelectedItem().toString().trim());
                    problem.setProblemLocation(spnrLocationSelect.getSelectedItem().toString().trim());
                    problem.setDescription(etDescription.getText().toString().trim());

                    Backendless.Persistence.save(problem, new AsyncCallback<ReportedProblem>() {
                        @Override
                        public void handleResponse(ReportedProblem response) {
                            Toast.makeText(getActivity(), "Report Submitted!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {

                            Toast.makeText(getActivity(), "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
                    spnrLocationSelect.setSelection(0);
                    spCategory.setSelection(0);
                    etDescription.setText(null);
                }
            }
        });

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }
}
