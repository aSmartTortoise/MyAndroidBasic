package com.jie.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class CustomSimpleViewModelFactory(app: Application, private val mData: String) :
    ViewModelProvider.AndroidViewModelFactory(app) {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return try {
            modelClass.getConstructor(String::class.java).newInstance(mData)
        } catch (e: Exception) {
            Log.d("CustormSimpleFactory", "create: wyj e:$e")
            return super.create(modelClass)
        }

        return super.create(modelClass)
    }
}