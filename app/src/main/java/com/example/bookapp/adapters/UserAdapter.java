package com.example.bookapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookapp.R;
import com.example.bookapp.models.User;
import com.google.android.material.chip.Chip;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private Context context;
    private List<User> userList;
    private OnUserClickListener clickListener;

    public interface OnUserClickListener {
        void onUserClick(User user);
        void onUserLongClick(User user);
    }

    public UserAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    public void setOnUserClickListener(OnUserClickListener listener) {
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);

        holder.tvUsername.setText(user.getUsername());
        holder.tvFullname.setText(user.getFullname());

        // Setup active status chip
        if (user.isActive()) {
            holder.chipActive.setText("Active");
            holder.chipActive.setChipBackgroundColorResource(R.color.active_green);
            holder.chipActive.setChipStrokeColorResource(R.color.active_green_dark);
        } else {
            holder.chipActive.setText("InActive");
            holder.chipActive.setChipBackgroundColorResource(R.color.inactive_red);
            holder.chipActive.setChipStrokeColorResource(R.color.inactive_red_dark);
        }

        // Setup role chip
        holder.chipRole.setText(user.getRole());
        if ("admin".equalsIgnoreCase(user.getRole())) {
            holder.chipRole.setChipBackgroundColorResource(R.color.role_admin);
        } else {
            holder.chipRole.setChipBackgroundColorResource(R.color.role_user);
        }

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onUserClick(user);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (clickListener != null) {
                clickListener.onUserLongClick(user);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername, tvFullname;
        Chip chipActive, chipRole;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvFullname = itemView.findViewById(R.id.tvFullname);
            chipActive = itemView.findViewById(R.id.chipActive);
            chipRole = itemView.findViewById(R.id.chipRole);
        }
    }
}