package com.jie.databinding.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableArrayMap
import androidx.databinding.ObservableMap
import com.jie.databinding.R
import com.jie.databinding.databinding.ActivityObservableCollectionBinding

class ObservableCollectionActivity : AppCompatActivity() {
    private lateinit var map: ObservableMap<String, String>;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val observableBinding = DataBindingUtil.setContentView<ActivityObservableCollectionBinding>(
            this, R.layout.activity_observable_collection
        )

        map = ObservableArrayMap()
        map.put("name", "frankie")
        map.put("age", "34")
        observableBinding.map = map
        val list = ObservableArrayList<String>()
        list.add("Frankie")
        list.add("34")
        observableBinding.list = list
        observableBinding.index = 1
        observableBinding.key = "name"
        title = "ObservableCollection study"
    }

    fun onClick(view: View) {
        map["name"] = "frankie ${(0..100).random()}"
    }
}