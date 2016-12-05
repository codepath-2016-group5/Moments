package com.codepath.apps.findmate.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.codepath.apps.findmate.R;
import com.codepath.apps.findmate.models.CheckIn;
import com.codepath.apps.findmate.models.ParseUsers;
import com.parse.ParseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

        viewHolder.ivInitials.setImageDrawable(getInitialsDrawable(checkIn.getCreator()));
        viewHolder.tvDate.setText(getRelativeTimeAgo(checkIn.getCreatedAt()));
        viewHolder.tvCreatorName.setText(ParseUsers.getName(checkIn.getCreator()));
        viewHolder.tvPlaceName.setText(checkIn.getPlace().getName());
        viewHolder.tvDescription.setText(checkIn.getDescription());
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return checkIns.size();
    }

    private TextDrawable getInitialsDrawable(ParseUser user) {
        String name = ParseUsers.getName(user);
        char initial = name.isEmpty() ? name.charAt(0) : user.getEmail().charAt(0);

        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color = generator.getColor(initial);

        TextDrawable.IBuilder builder = TextDrawable.builder()
                .beginConfig()
                .withBorder(4)
                .toUpperCase()
                .endConfig()
                .round();

        return builder.build(Character.toString(initial), color);
    }

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    private String getRelativeTimeAgo(Date date) {
        long dateMillis = date.getTime();
        return DateUtils.getRelativeTimeSpanString(dateMillis,
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivInitials;
        TextView tvDate;
        TextView tvCreatorName;
        TextView tvPlaceName;
        TextView tvDescription;

        ViewHolder(View itemView) {
            super(itemView);

            ivInitials = (ImageView) itemView.findViewById(R.id.ivInitials);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            tvCreatorName = (TextView) itemView.findViewById(R.id.tvCreatorName);
            tvPlaceName = (TextView) itemView.findViewById(R.id.tvPlaceName);
            tvDescription = (TextView) itemView.findViewById(R.id.tvDescription);
        }
    }
}
