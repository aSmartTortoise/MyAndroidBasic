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

        fun toBothWayBinding() {
            reference?.get()?.run {
                startActivity(Intent(this, BothWayBindingActivity::class.java))
            }
        }

        fun toEventBinding() {
            reference?.get()?.run {
                startActivity(Intent(this, EventBindingActivity::class.java))
            }
        }

        fun toClassMethod() {
            reference?.get()?.run {
                startActivity(Intent(this, ClassMethodActivity::class.java))
            }
        }

        fun toOperator() {
            reference?.get()?.run { startActivity(Intent(this, OperatorActivity::class.java)) }
        }

        fun toIncludeViewStub() {
            reference?.get()?.run { startActivity(Intent(this, IncludeViewStubActivity::class.java)) }
        }

        fun toViewStubInflate() {
            reference?.get()?.run {
                startActivity(Intent(this, ViewStubInflateActivity::class.java))
            }
        }

        fun toBindingAdapter() {
            reference?.get()?.run {
                startActivity(Intent(this, BindingAdapterConversionActivity::class.java))
            }
        }

        fun toCollection() {
            reference?.get()?.run {
                startActivity(Intent(this, CollectionActivity::class.java))
            }
        }

        fun toRecyclerView() {
            reference?.get()?.run {
                startActivity(Intent(this, RecyclerViewActivity::class.java))
            }
        }
    }
}