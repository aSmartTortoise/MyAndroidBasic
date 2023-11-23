package com.jie.flow

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.jie.flow.databinding.ActivityCheesesBinding

open class BaseSearchActivity: AppCompatActivity() {
    protected val cheeseSearchEngine by lazy {
        val cheeses = listOf(*resources.getStringArray(R.array.cheeses))
        CheeseSearchEngine(cheeses)
    }

    lateinit var dataBinding: ActivityCheesesBinding
    lateinit var cheeseAdapter: CheeseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_cheeses)
        cheeseAdapter = CheeseAdapter()
        dataBinding.list.apply {
            layoutManager = LinearLayoutManager(this@BaseSearchActivity)
            adapter = cheeseAdapter
        }
    }


}