package com.jie.databinding.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.jie.databinding.R
import com.jie.databinding.databinding.ActivityViewStub2Binding
import com.jie.databinding.databinding.ViewStubBinding
import com.jie.databinding.model.UserEntity

class ViewStubInflateActivity : AppCompatActivity() {
    private lateinit var viewStub2Binding: ActivityViewStub2Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewStub2Binding = DataBindingUtil.setContentView<ActivityViewStub2Binding>(
            this,
            R.layout.activity_view_stub2
        ).apply {
            presenter = Presenter()
            val userInfo = UserEntity("Frankie", "123654")
            viewStub.setOnInflateListener { stub, inflated ->
                DataBindingUtil.bind<ViewStubBinding>(inflated)?.apply {
                    setUserInfo(userInfo)
                }
            }
        }
        title = "viewStub bind entity study"
    }

    inner class Presenter {
        fun onClick(view: View) {
            if (!viewStub2Binding.viewStub.isInflated) {
                viewStub2Binding.viewStub.viewStub?.inflate()
            }
        }
    }
}