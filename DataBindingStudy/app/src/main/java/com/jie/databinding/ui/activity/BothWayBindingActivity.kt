package com.jie.databinding.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.jie.databinding.R
import com.jie.databinding.databinding.ActivityBothWayBindingBinding
import com.jie.databinding.model.ObservableGoodsEntity

class BothWayBindingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityBothWayBindingBinding>(
            this,
            R.layout.activity_both_way_binding
        ).apply {
            goods = ObservableGoodsEntity(
                "DogeCoin",
                "DogeCoin is a wonderful crypton, you can own it",
                0.19F
            )
        }
        title = "both-way binding study"
    }
}