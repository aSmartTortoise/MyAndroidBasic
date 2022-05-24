package com.jie.databinding.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.SparseArray
import androidx.databinding.DataBindingUtil
import com.jie.databinding.R
import com.jie.databinding.databinding.ActivityCollectionBinding

class CollectionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityCollectionBinding>(
            this,
            R.layout.activity_collection
        ).apply {
            index = 0
            key = "jackie"
            val strArr = arrayOf("Hi", "Hello")
            val list = mutableListOf("Hello")
            val map = mutableMapOf(Pair<String, String>("jackie", "Hi"))
            val set = mutableSetOf("Hi")
            val sparseArr: SparseArray<String> = SparseArray()
            sparseArr.put(0, "Hello")
            array = strArr
            setList(list)
            setMap(map)
            setSet(set)
            sparse = sparseArr
        }
        title = "Collection in dataBinding study"
    }
}