package com.jie.databinding.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.jie.databinding.R
import com.jie.databinding.databinding.ActivityBindingAdapterConversionBinding
import com.jie.databinding.model.ImageEntity

class BindingAdapterConversionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityBindingAdapterConversionBinding>(
            this,
            R.layout.activity_binding_adapter_conversion
        ).apply {
            imageInfo = ImageEntity("baidu")
            presenter = Presenter()
        }
        title = "BindingAdapter study"
    }

    inner class Presenter {
        fun onClick(image: ImageEntity): Boolean {
            image.urlField?.set("xxxJackie ${(0..1000).random()}")
            return true
        }
    }
}