package com.jie.databinding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.jie.databinding.databinding.ItemUserBinding
import com.jie.databinding.model.UserEntity

class UserAdapter(userInfos: MutableList<UserEntity>?) :
    RecyclerView.Adapter<UserAdapter.UserHolder>() {
    private var users: MutableList<UserEntity>? = null

    init {
        users = mutableListOf()
        if (userInfos != null && !userInfos.isEmpty()) {
            users!!.addAll(userInfos)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {
        DataBindingUtil.inflate<ItemUserBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_user,
            parent,
            false
        ).apply {
            return UserHolder(this)
        }
    }

    override fun onBindViewHolder(holder: UserHolder, position: Int) {
        holder.itemUserBinding.userInfo = users?.get(position)
    }

    override fun getItemCount(): Int {
        return users?.size ?: 0
    }

    inner class UserHolder(var itemUserBinding: ItemUserBinding) : RecyclerView.ViewHolder(
        itemUserBinding.root
    )
}