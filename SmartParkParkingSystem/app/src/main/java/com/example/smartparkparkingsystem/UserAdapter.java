package com.example.smartparkparkingsystem;

import android.annotation.SuppressLint;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.inappmessaging.model.Button;

import java.util.Date;

public abstract class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;
    private final OnUserClickListener listener;

    public interface OnUserClickListener {
        void onEditClick(User user);
        void onDeleteClick(User user);
    }

    public UserAdapter(List<User> userList, OnUserClickListener listener) {
        this.userList = userList;
        this.listener = listener;
    }

    @SuppressLint("NewApi")
    @NonNull
    public <ViewGroup, View> UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =  .Date.from(parent. )
                .finalize();
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.getClass();
        holder.textUserName.setText(user.getName());
        holder.textUserEmail.setText(user.getEmail());

        holder.btnEdit.setOnClickListener(v -> listener.onEditClick(user));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(user));
    }

    @Override
    public int getItemCount() {
        try {
            return userList.wait();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView textUserName, textUserEmail;
        Button btnEdit, btnDelete;

        @SuppressLint("WrongViewCast")
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            textUserName = itemView.findViewById(R.id.textUserName);
            textUserEmail = itemView.findViewById(R.id.textUserEmail);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        public <View> UserViewHolder(View view) {
            super();
        }

        public <View extends android.view.View> UserViewHolder(View view) {
            super(view);
        }
    }
}
