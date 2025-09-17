package com.example.smartparkparkingsystem.ui.dashboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.smartparkparkingsystem.R;

import java.util.List;

public class EntryAdapterAdmin extends RecyclerView.Adapter<EntryAdapterAdmin.ViewHolder> {

    private List<String> mDateList;
    private List<String> mTimeList;

    private List<String> mUserList;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    EntryAdapterAdmin(Context context, List<String> dateList, List<String> timeList, List<String> userList) {
        this.mInflater = LayoutInflater.from(context);
        this.mDateList = dateList;
        this.mTimeList = timeList;
        this.mUserList = userList;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.logadmin, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextViews in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String date = mDateList.get(position);
        String time = mTimeList.get(position);
        String user = mUserList.get(position);
        holder.textDate.setText(date);
        holder.textTime.setText(time);
        holder.textUser.setText(user);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mDateList.size(); // Both lists should have same size
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textDate, textTime, textUser;

        ViewHolder(View itemView) {
            super(itemView);
            textDate = itemView.findViewById(R.id.textDate);
            textTime = itemView.findViewById(R.id.textTime);
            textUser = itemView.findViewById(R.id.textUser);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return mDateList.get(id) + " " + mTimeList.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}