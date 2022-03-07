package com.jie.viewmodel2.request.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jie.viewmodel2.bean.User
import com.jie.viewmodel2.request.repository.MainRepository

class MainViewModel(private val mRepository: MainRepository) : ViewModel() {
    private val _user: MutableLiveData<User> = MutableLiveData()
    val mUsers: LiveData<User> = _user

    init {
        _user.postValue(User(1, "测试1"))
    }

}