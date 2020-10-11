package com.app.tracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    private List<LocationData> users;

    Adapter(List<LocationData> users) {
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.text_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return users != null ? users.size() : 0;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.sNo.setText(String.valueOf(position + 1));
        holder.ssid.setText(String.valueOf(users.get(position).getLat()));
        holder.bssid.setText(String.valueOf(users.get(position).getLng()));
        Calendar calendar=Calendar.getInstance();
        calendar.setTimeInMillis(users.get(position).getTime());
        holder.time.setText(new SimpleDateFormat("DD MMM hh:mm:ss a").format(calendar.getTime()));
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView sNo;
        TextView ssid;
        TextView bssid;
        TextView time;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            sNo = itemView.findViewById(R.id.tv_s_no);
            ssid = itemView.findViewById(R.id.tv_ssid);
            bssid = itemView.findViewById(R.id.tv_bssid);
            time = itemView.findViewById(R.id.tv_time);

        }

        public void setTextColor(int color) {
            sNo.setTextColor(color);
            ssid.setTextColor(color);
            bssid.setTextColor(color);
            time.setTextColor(color);
        }

    }
}