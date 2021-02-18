package com.example.reinstall_app.activity_classes;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.example.reinstall_app.R;
import com.example.reinstall_app.app_data.ProblemType;
import com.example.reinstall_app.app_data.ReinstallApplicationClass;
import com.example.reinstall_app.app_data.ReportedProblem;
import com.example.reinstall_app.app_data.Suburb;

import java.util.List;

public class StatsAdapter extends RecyclerView.Adapter<StatsAdapter.ViewHolder> {


    private List<Suburb> statsList;
    StatsItemClicked activity;

    int adapterPos;

    String problemSelected, suburbSelected="Kry nie suburb nie", filteredProbleSelected="default";

    public interface StatsItemClicked
    {
        void onStatsItemClicked(int index);
    }

    public StatsAdapter(Context context, List<Suburb> list)
    {
        statsList = list;
        activity = (StatsItemClicked) context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {

        TextView tvSuburb;
        ImageButton ivPopupMenu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvSuburb = itemView.findViewById(R.id.tvSuburb);
            ivPopupMenu = itemView.findViewById(R.id.ivPopupMenu);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    activity.onStatsItemClicked(statsList.indexOf((Suburb) v.getTag()));

                }
            });
        }
    }

    @NonNull
    @Override
    public StatsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.stats_row_layout, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull StatsAdapter.ViewHolder holder, int position) {

        holder.itemView.setTag(statsList.get(position));

        holder.tvSuburb.setText(statsList.get(position).getSuburbName());


        //Stats popup menu
        //-----------------------------------------------------------------------------

        holder.ivPopupMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popup = new PopupMenu(holder.ivPopupMenu.getContext(), v);

                adapterPos = holder.getAdapterPosition();

                //Get Selected Suburb's name
                //-----------------------------------------------------------------------------------------------------------

                DataQueryBuilder ptQueryBuilder = DataQueryBuilder.create();
                String whereClause="totalReports > 0";
                ptQueryBuilder.setSortBy("suburbName");
                ptQueryBuilder.setWhereClause(whereClause);


                suburbSelected = statsList.get(position).getSuburbName();


                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        MenuItem menuPos;

                        menuPos=menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {

                                menuItem.getItemId();

                                return false;
                            }
                        });


                        problemSelected = menuPos.toString().trim();

                        DataQueryBuilder stringCycle = DataQueryBuilder.create();
                        int PAGESIZE = 80;
                        stringCycle.setPageSize(PAGESIZE);

                        Backendless.Persistence.of(ProblemType.class).find(stringCycle, new AsyncCallback<List<ProblemType>>() {
                            @Override
                            public void handleResponse(List<ProblemType> response) {

                                for(int i=0; i<response.size(); i++)
                                {
                                    if(problemSelected.contains(response.get(i).getProblemName()))
                                    {
                                        filteredProbleSelected = response.get(i).getProblemName().trim();
                                       // Toast.makeText(holder.ivPopupMenu.getContext(), "MenuPos: "+menuPos, Toast.LENGTH_SHORT).show();

                                        Intent intent=new Intent(holder.ivPopupMenu.getContext(), ProblemStatsMap.class);

                                        intent.putExtra("problemSelected", filteredProbleSelected);
                                        intent.putExtra("suburbSelected", suburbSelected);

                                        holder.ivPopupMenu.getContext().startActivity(intent);

                                    }
                                }
                                if(problemSelected.isEmpty())
                                {
                                    Toast.makeText(holder.ivPopupMenu.getContext(), "problemSelected EMPTY", Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void handleFault(BackendlessFault fault) {

                                Toast.makeText(holder.ivPopupMenu.getContext(), "Error: "+fault.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });

                        return false;
                    }
                });

                popup.inflate(R.menu.problem_type_popup_menu);


                //Populate popup menu with items retrieved form database
                //-----------------------------------------------------------------------------------------------------------

                DataQueryBuilder qryBuilder = DataQueryBuilder.create();
                String whereClause2="suburb = '" + ReinstallApplicationClass.hotspotList.get(adapterPos).getSuburbName() + "'";
                //qryBuilder.setProperties("Count(objectId) as totalProblems");
                //qryBuilder.setGroupBy("problemType");
                qryBuilder.setWhereClause(whereClause2);
                // qryBuilder.setSortBy("problemType");

                Backendless.Data.of(ReportedProblem.class).find(qryBuilder, new AsyncCallback<List<ReportedProblem>>() {
                    @Override
                    public void handleResponse(List<ReportedProblem> response) {

                        int counter=0;

                        for(int j=0; j<ReinstallApplicationClass.problemTypes.size(); j++)
                        {
                            for(int i=0; i<response.size(); i++)
                            {

                                if(response.get(i).getProblemType().equals(ReinstallApplicationClass.problemTypes.get(j).getProblemName()) && !response.get(i).isResolved() && !response.get(i).isFakeReport() )
                                {
                                    counter += 1;
                                }

                            }

                            if(counter>0)
                            {
                                String popupItem=ReinstallApplicationClass.problemTypes.get(j).getProblemName();
                                popup.getMenu().add(popupItem +" : "+counter);
                                counter = 0;
                            }
                        }
                    }
                    @Override
                    public void handleFault(BackendlessFault fault) {

                        Toast.makeText(holder.ivPopupMenu.getContext(), "Error: "+fault.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });


                popup.show();

            }
        });


    }

    @Override
    public int getItemCount() {
        return statsList.size();
    }
}
