package com.example.beatflow;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.beatflow.Data.User;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> allUsers;
    private List<User> filteredUsers;
    private OnUserClickListener clickListener;

    public UserAdapter(List<User> users, OnUserClickListener clickListener) {
        this.allUsers = new ArrayList<>(users);
        this.filteredUsers = new ArrayList<>(users);
        this.clickListener = clickListener;
    }

    public void updateUsers(List<User> newUsers) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new UserDiffCallback(this.filteredUsers, newUsers));
        this.allUsers = new ArrayList<>(newUsers);
        this.filteredUsers = new ArrayList<>(newUsers);
        diffResult.dispatchUpdatesTo(this);
        Log.d("UserAdapter", "Updated users list. New size: " + this.filteredUsers.size());
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = filteredUsers.get(position);
        holder.bind(user, clickListener);
        Log.d("UserAdapter", "Binding user at position " + position + ": " + user.getName() + ", ID: " + user.getId());
    }

    @Override
    public int getItemCount() {
        return filteredUsers.size();
    }
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String filterPattern = constraint.toString().toLowerCase().trim();

                List<User> filteredList;
                if (filterPattern.isEmpty()) {
                    filteredList = new ArrayList<>(allUsers);
                } else {
                    filteredList = new ArrayList<>();
                    for (User user : allUsers) {
                        if (user.getName().toLowerCase().contains(filterPattern)) {
                            filteredList.add(user);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredUsers = (List<User>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView cardView;
        private final ShapeableImageView userImage;
        private final TextView userName;
        private final TextView userDescription;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (MaterialCardView) itemView;
            userImage = itemView.findViewById(R.id.userImage);
            userName = itemView.findViewById(R.id.userName);
            userDescription = itemView.findViewById(R.id.userDescription);
        }

        void bind(final User user, final OnUserClickListener clickListener) {
            userName.setText(user.getName());
            userDescription.setText(user.getDescription());

            if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(user.getProfileImageUrl())
                        .placeholder(R.drawable.circular_image)
                        .error(R.drawable.error_profile_image)
                        .circleCrop()
                        .into(userImage);
            } else {
                userImage.setImageResource(R.drawable.error_profile_image);
            }

            cardView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onUserClick(user);
                }
            });

            Log.d("UserViewHolder", "Bound user: " + user.getName() + ", ID: " + user.getId());
        }
    }

    public interface OnUserClickListener {
        void onUserClick(User user);
    }

    private static class UserDiffCallback extends DiffUtil.Callback {
        private final List<User> oldUsers;
        private final List<User> newUsers;

        UserDiffCallback(List<User> oldUsers, List<User> newUsers) {
            this.oldUsers = oldUsers;
            this.newUsers = newUsers;
        }

        @Override
        public int getOldListSize() {
            return oldUsers.size();
        }

        @Override
        public int getNewListSize() {
            return newUsers.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldUsers.get(oldItemPosition).getId().equals(newUsers.get(newItemPosition).getId());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldUsers.get(oldItemPosition).equals(newUsers.get(newItemPosition));
        }
    }
}