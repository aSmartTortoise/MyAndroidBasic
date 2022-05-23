package com.jie.databinding.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.jie.databinding.R
import com.jie.databinding.databinding.ActivityIncludeViewStubBinding
import com.jie.databinding.model.UserEntity

class IncludeViewStubActivity : AppCompatActivity() {
    private lateinit var includeViewStubBinding: ActivityIncludeViewStubBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        includeViewStubBinding = DataBindingUtil.setContentView<ActivityIncludeViewStubBinding>(
            this,
            R.layout.activity_include_view_stub
        ).apply {
            useInfo = UserEntity("Frankie", "123456")
            presenter = Presenter()
        }
        title = "include viewStub study"
    }

    inner class Presenter {
        fun onClick(view: View) {
            if (!includeViewStubBinding.viewStub.isInflated) {
                includeViewStubBinding.viewStub.viewStub?.inflate()
            }
        }
    }
}