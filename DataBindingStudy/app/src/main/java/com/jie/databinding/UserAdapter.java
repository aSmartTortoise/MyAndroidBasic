package com.jie.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jie.databinding.databinding.ItemUserBinding;
import com.jie.databinding.model.User;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserHolder> {
    private List<User> mUserInfos;

    public UserAdapter(List<User> userInfos) {
        mUserInfos = new ArrayList<>();
        mUserInfos.addAll(userInfos);
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUserBinding userBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.item_user, parent, false);

        return new UserHolder(userBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserHolder holder, int position) {
        holder.getUserBinding().setUserInfo(mUserInfos.get(position));
    }


    @Override
    public int getItemCount() {
        return mUserInfos.size();
    }

    class UserHolder extends RecyclerView.ViewHolder {
        private ItemUserBinding mUserBinding;

        public UserHolder( ItemUserBinding userBinding) {
            super(userBinding.getRoot());
            mUserBinding = userBinding;
        }

        public ItemUserBinding getUserBinding() {
            return mUserBinding;
        }
    }
}
