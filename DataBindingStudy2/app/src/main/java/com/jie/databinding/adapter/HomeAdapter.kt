package com.jie.databinding.adapter

import com.jie.databinding.base.BaseAdapter
import com.jie.databinding.databinding.ItemHomeBinding
import com.jie.databinding.interf.ItemClickListener

class HomeAdapter(private val listener: ItemClickListener<String>) : BaseAdapter<String, ItemHomeBinding>() {

    override fun ItemHomeBinding.setListener() {
        itemClickListener = listener
    }
    override fun ItemHomeBinding.onBindViewHolder(bean: String, position: Int) {
        this.bean = bean
        this.position = position
        tv.text = bean
    }
}