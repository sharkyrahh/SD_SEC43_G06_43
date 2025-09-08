package com.example.smartparkparkingsystem.ui.dashboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartparkparkingsystem.R;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private Context context;
    private List<User> userList;
    private final OnUserClickListener listener;

    public interface OnUserClickListener {
        void onEditClick(User user);

        void onDeleteClick(User user);
    }

    public UserAdapter(Context context, List<User> userList, OnUserClickListener listener) {
        this.context = context;
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.textUserName.setText(user.getName());
        holder.textUserEmail.setText(user.getEmail());

        holder.btnEdit.setOnClickListener(v -> listener.onEditClick(user));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(user));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void updateList(List<User> newList) {
        userList = newList;
        notifyDataSetChanged();
    }


    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView textUserName, textUserEmail;
        ImageButton btnEdit, btnDelete;
            public UserViewHolder(@NonNull View itemView) {
                super(itemView);
                textUserName = itemView.findViewById(R.id.textUserName);
                textUserEmail = itemView.findViewById(R.id.textUserEmail);
                btnEdit = itemView.findViewById(R.id.btnEdit);
                btnDelete = itemView.findViewById(R.id.btnDelete);
            }
        }
    }

