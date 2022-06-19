package com.jie.livedata.global

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.jie.livedata.R
import com.jie.livedata.databinding.ActivityNotifyLiveDataBinding

class NotifyLiveDataActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val liveDataBinding = DataBindingUtil.setContentView<ActivityNotifyLiveDataBinding>(
            this,
            R.layout.activity_notify_live_data
        )
        liveDataBinding.btnToFragmentA.setOnClickListener {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fl_container, FragmentA())
                .commit()
        }
        liveDataBinding.btnToFragmentB.setOnClickListener {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fl_container, FragmentB())
                .commit()
        }
    }
}