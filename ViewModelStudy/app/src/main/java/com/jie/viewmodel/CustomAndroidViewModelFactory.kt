package com.jie.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class CustomAndroidViewModelFactory(val app: Application, private val mData: String) :
    ViewModelProvider.AndroidViewModelFactory(app) {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (AndroidViewModel::class.java.isAssignableFrom(modelClass)) {
            return try {
                modelClass.getConstructor(Application::class.java, String::class.java)
                    .newInstance(app, mData)
            } catch (e: Exception) {
                Log.e("CustomViewModelFactory", "create: wyj new instance error")
                return super.create(modelClass)
            }
        }
        return super.create(modelClass)
    }
}