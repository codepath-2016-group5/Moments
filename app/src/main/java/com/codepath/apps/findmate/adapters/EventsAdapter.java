package com.codepath.apps.findmate.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.findmate.R;
import com.codepath.apps.findmate.models.Event;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {

    private Context context;
    private List<Event> events;

    public EventsAdapter(Context context, List<Event> events) {
        this.context = context;
        this.events = events;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View eventView = inflater.inflate(R.layout.item_event, parent, false);
        return new ViewHolder(eventView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Event event = events.get(position);

        if (event.getPictureUrl() != null) {
            Picasso.with(context).load(event.getPictureUrl()).into(holder.ivEventImage);
        } else {
            holder.ivEventImage.setImageResource(0);
        }

        Calendar calendar = Calendar.getInstance();
        Date start = event.getStart() == null ? new Date() : event.getStart();
        calendar.setTime(start);
        holder.tvEventDay.setText(Integer.toString(calendar.get(Calendar.DAY_OF_MONTH)));
        holder.tvEventMonth.setText(new SimpleDateFormat("MMM").format(calendar.getTime()));

        holder.tvEventName.setText(event.getName());
        holder.tvEventDatePlace.setText(event.getDetails());
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivEventImage;
        public TextView tvEventDay;
        public TextView tvEventMonth;
        public TextView tvEventName;
        public TextView tvEventDatePlace;

        public ViewHolder(View itemView) {
            super(itemView);

            ivEventImage = (ImageView) itemView.findViewById(R.id.ivEventImage);
            tvEventDay = (TextView) itemView.findViewById(R.id.tvEventDay);
            tvEventMonth = (TextView) itemView.findViewById(R.id.tvEventMonth);
            tvEventName = (TextView) itemView.findViewById(R.id.tvEventName);
            tvEventDatePlace = (TextView) itemView.findViewById(R.id.tvEventDatePlace);
        }
    }
}
