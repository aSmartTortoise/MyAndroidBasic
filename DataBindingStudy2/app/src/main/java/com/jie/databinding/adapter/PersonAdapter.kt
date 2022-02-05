package com.jie.databinding.adapter

import android.util.Log
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.jie.databinding.base.BaseMultiTypeAdapter
import com.jie.databinding.bean.Person
import com.jie.databinding.bean.Student
import com.jie.databinding.bean.Teacher
import com.jie.databinding.databinding.ItemPersonBinding
import com.jie.databinding.databinding.ItemStudentBinding
import com.jie.databinding.databinding.ItemTeacherBinding

class PersonAdapter : BaseMultiTypeAdapter<Person>() {

    companion object {
        private const val ITEM_DEFAULT_TYPE = 0
        private const val ITEM_STUDENT_TYPE = 1
        private const val ITEM_TEACHER_TYPE = 2
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Student -> ITEM_STUDENT_TYPE
            is Teacher -> ITEM_TEACHER_TYPE
            else -> ITEM_DEFAULT_TYPE
        }
    }

    override fun onCreateMultiViewHolder(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return when (viewType) {
            ITEM_STUDENT_TYPE -> loadLayout(ItemStudentBinding::class.java, parent)
            ITEM_TEACHER_TYPE -> loadLayout(ItemTeacherBinding::class.java, parent)
            else -> loadLayout(ItemPersonBinding::class.java, parent)
        }
    }

    override fun MultiTypeViewHolder.onBindViewHolder(
        holder: MultiTypeViewHolder,
        item: Person,
        position: Int
    ) {
        when (holder.mBinding) {
            is ItemStudentBinding -> {
                Log.d("ItemStudentBinding", "item : $item   position : $position")
                (mBinding as ItemStudentBinding).tvName.text = item.name
            }
            is ItemTeacherBinding -> {
                Log.d("ItemTeacherBinding", "item : $item   position : $position")
                (mBinding as ItemTeacherBinding).tvName.text = item.name
            }
            else -> {
                (mBinding as ItemPersonBinding).tvName.text = item.name
            }
        }
    }
}