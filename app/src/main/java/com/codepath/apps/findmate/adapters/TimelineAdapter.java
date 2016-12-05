package com.codepath.apps.findmate.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.codepath.apps.findmate.R;
import com.codepath.apps.findmate.models.CheckIn;
import com.codepath.apps.findmate.models.ParseUsers;

import java.util.List;

public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.ViewHolder> {

    private List<CheckIn> checkIns;
    private Context context;

    public TimelineAdapter(Context context, List<CheckIn> checkIns) {
        this.checkIns = checkIns;
        this.context = context;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return context;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public TimelineAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.item_check_in, parent, false);
        return new ViewHolder(contactView);
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(TimelineAdapter.ViewHolder viewHolder, int position) {
        CheckIn checkIn = checkIns.get(position);

        viewHolder.tvCreatorName.setText(ParseUsers.getName(checkIn.getCreator()));
        viewHolder.tvPlaceName.setText(checkIn.getPlace().getName());
        viewHolder.tvDescription.setText(checkIn.getDescription());
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return checkIns.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvCreatorName;
        public TextView tvPlaceName;
        public TextView tvDescription;

        public ViewHolder(View itemView) {
            super(itemView);

            tvCreatorName = (TextView) itemView.findViewById(R.id.tvCreatorName);
            tvPlaceName = (TextView) itemView.findViewById(R.id.tvPlaceName);
            tvDescription = (TextView) itemView.findViewById(R.id.tvDescription);
        }
    }
}
