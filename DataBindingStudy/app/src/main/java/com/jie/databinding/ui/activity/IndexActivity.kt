package com.jie.databinding.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.jie.databinding.R
import com.jie.databinding.databinding.ActivityIndexBinding
import com.jie.databinding.model.UserEntity
import java.lang.ref.WeakReference

class IndexActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val indexBinding: ActivityIndexBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_index)
        indexBinding.userInfo = UserEntity("Frankie", "123568")
        indexBinding.dataHandler = DataHandler(WeakReference(this))
    }

    inner class DataHandler(var reference: WeakReference<IndexActivity>?) {
        fun toBaseObservable() {
            reference?.get()?.run {
                startActivity(Intent(this, BaseObservableActivity::class.java))
            }
        }

        fun toObservableField() {
            reference?.get()?.run {
                startActivity(Intent(this, ObservableFieldActivity::class.java))
            }
        }

        fun toObservableCollection() {
            reference?.get()?.run {
                startActivity(Intent(this, ObservableCollectionActivity::class.java))
            }
        }
    }
}