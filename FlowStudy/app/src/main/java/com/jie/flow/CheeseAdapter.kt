package com.jie.flow

import android.R
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CheeseAdapter : RecyclerView.Adapter<CheeseAdapter.CheeseViewHolder>() {
    var cheeses: MutableList<String>? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setCheese(cheeses: List<String>) {
        this.cheeses?.let {
            it.clear()
            it.addAll(cheeses)
        } ?: run {
            this.cheeses = mutableListOf<String>().apply {
                addAll(cheeses)
            }
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheeseViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.simple_list_item_1, parent, false)
        return CheeseViewHolder(view)
    }

    override fun onBindViewHolder(holder: CheeseViewHolder, position: Int) {
        holder.title.text = cheeses?.get(position)
    }

    override fun getItemCount() = cheeses?.size ?: 0

    inner class CheeseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView

        init {
            title = itemView.findViewById<View>(R.id.text1) as TextView
        }
    }

}