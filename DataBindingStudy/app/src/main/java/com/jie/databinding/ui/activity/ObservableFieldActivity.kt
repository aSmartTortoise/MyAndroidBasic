package com.jie.databinding.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.jie.databinding.R
import com.jie.databinding.databinding.ActivityObservableFieldBinding
import com.jie.databinding.model.ObservableGoodsEntity
import java.lang.ref.WeakReference

class ObservableFieldActivity : AppCompatActivity() {
    private lateinit var goods: ObservableGoodsEntity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fieldBinding = DataBindingUtil.setContentView<ActivityObservableFieldBinding>(this, R.layout.activity_observable_field)
        goods = ObservableGoodsEntity("dogeCoin yeah", "wa, wonderfull coin", 0.15F)
        fieldBinding.goods = goods
        fieldBinding.dataHandler = DataHandler(this)
    }

    inner class DataHandler(activity: ObservableFieldActivity) {
        private var mReference: WeakReference<ObservableFieldActivity>? = null
        init {
            mReference = WeakReference(activity)
        }

        fun changeGoodsName() {
            mReference?.let {
                it.get()?.goods?.name?.set("DogeCoin super ${(0..100).random()}")
            }
        }

        fun changeGoodsDetails() {
            mReference?.get()?.goods?.details?.set("In 2022, Elon Musk said: DogeCoin can by some Tesla Belonings. ${(0..100).random()}")
        }
    }
}