package com.jie.viewmodel2.request

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jie.viewmodel2.request.repository.MainRepository
import com.jie.viewmodel2.request.viewmodel.MainViewModel

fun provideMainViewModelFactory(): MainViewModelFactory {
    return MainViewModelFactory(MainRepository())
}

class MainViewModelFactory(private val mRepository: MainRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainViewModel(mRepository) as T
    }

}