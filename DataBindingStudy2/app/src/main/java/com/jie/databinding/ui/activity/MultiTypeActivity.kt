package com.jie.databinding.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jie.databinding.adapter.PersonAdapter
import com.jie.databinding.base.BaseActivity
import com.jie.databinding.bean.Student
import com.jie.databinding.bean.Teacher
import com.jie.databinding.databinding.ActivityMultiTypeBinding

class MultiTypeActivity : BaseActivity<ActivityMultiTypeBinding>() {
    override fun ActivityMultiTypeBinding.initBinding() {
        val personAdapter = PersonAdapter()
        with(recyclerView) {
            layoutManager = LinearLayoutManager(this@MultiTypeActivity).apply {
                orientation = RecyclerView.VERTICAL
            }
            adapter = personAdapter
        }

        personAdapter.setData(
            listOf(
                Teacher(1,"Person","语文"),
                Student(2,"Person","一年级"),
                Teacher(3,"Person","数学"),
            ))

        setTitle("multiTypeAdapter")

    }
}