package com.example.reinstall_app.activity_classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reinstall_app.R;
import com.example.reinstall_app.app_data.ReportedProblem;

import java.util.List;

public class StatsAdapter extends RecyclerView.Adapter<StatsAdapter.ViewHolder> {


    private List<ReportedProblem> statsList;
    ItemClicked activity;

    public interface ItemClicked
    {
        void onItemClicked(int index);
    }

    public StatsAdapter(Context context, List<ReportedProblem> list)
    {
        statsList = list;
        activity = (ItemClicked) context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {

        TextView tvProblemType, tvNumReportsProblem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvProblemType = itemView.findViewById(R.id.tvProblemType);
            tvNumReportsProblem = itemView.findViewById(R.id.tvNumReportsProblems);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    activity.onItemClicked(statsList.indexOf((ReportedProblem) v.getTag()));

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

        holder.tvProblemType.setText(statsList.get(position).getProblemType());
        holder.tvNumReportsProblem.setText(statsList.get(position).getSuburb());

    }

    @Override
    public int getItemCount() {
        return statsList.size();
    }
}
