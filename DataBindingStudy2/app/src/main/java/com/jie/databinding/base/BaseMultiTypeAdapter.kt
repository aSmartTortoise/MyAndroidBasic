package com.jie.databinding.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import java.util.zip.Inflater

abstract class BaseMultiTypeAdapter<T> :
    RecyclerView.Adapter<BaseMultiTypeAdapter.MultiTypeViewHolder>() {
    private var mData: List<T> = mutableListOf()

    fun setData(data: List<T>?) {
        data?.let {
            val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize(): Int {
                    return mData.size
                }

                override fun getNewListSize(): Int {
                    return it.size
                }

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    val oldItem = mData.get(oldItemPosition)
                    val newItem = it.get(newItemPosition)
                    return this@BaseMultiTypeAdapter.areItemsTheSame(oldItem, newItem)
                }

                override fun areContentsTheSame(
                    oldItemPosition: Int,
                    newItemPosition: Int
                ): Boolean {
                    val oldItem = mData.get(oldItemPosition)
                    val newItem = it.get(newItemPosition)
                    return this@BaseMultiTypeAdapter.areItemContentsTheSame(
                        oldItem,
                        newItem,
                        oldItemPosition,
                        newItemPosition
                    )
                }

            })
            mData = data
            result.dispatchUpdatesTo(this)
        } ?: let {
            mData = mutableListOf()
            notifyItemRangeChanged(0, mData.size)
        }
    }

    fun addData(data: List<T>, position: Int? = null) {
        if (!data.isNullOrEmpty()) {
            with(LinkedList(mData)) {
                position?.let {
                    val startPosition = when {
                        it < 0 -> 0
                        it > size -> size
                        else -> it
                    }
                    addAll(startPosition, data)
                } ?: addAll(data)
                setData(this)
            }
        }
    }

    protected open fun areItemContentsTheSame(
        oldItem: T,
        newItem: T,
        oldItemPosition: Int,
        newItemPosition: Int
    ): Boolean {
        return oldItem == newItem
    }

    protected open fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }

    fun getData(): List<T> {
        return mData
    }

    fun getItem(position: Int): T {
        return mData[position]
    }

    fun getActualPosition(data: T): Int {
        return mData.indexOf(data)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MultiTypeViewHolder {
        return MultiTypeViewHolder(onCreateMultiViewHolder(parent, viewType))
    }

    override fun onBindViewHolder(holder: MultiTypeViewHolder, position: Int) {
        holder.onBindViewHolder(holder, getItem(position), position)
        holder.mBinding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    abstract fun onCreateMultiViewHolder(parent: ViewGroup, viewType: Int): ViewDataBinding

    abstract fun MultiTypeViewHolder.onBindViewHolder(
        holder: MultiTypeViewHolder,
        item: T,
        position: Int
    )

    protected fun <VB : ViewDataBinding> loadLayout(vbClass: Class<VB>, parent: ViewGroup): VB {
        val inflate = vbClass.getDeclaredMethod(
            "inflate",
            LayoutInflater::class.java,
            ViewGroup::class.java,
            Boolean::class.java
        )
        return inflate.invoke(null, LayoutInflater.from(parent.context), parent, false) as VB
    }

    class MultiTypeViewHolder(var mBinding: ViewDataBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

    }
}