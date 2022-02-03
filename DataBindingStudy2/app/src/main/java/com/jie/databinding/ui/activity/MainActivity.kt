package com.jie.databinding.ui.activity

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jie.databinding.adapter.HomeAdapter
import com.jie.databinding.base.BaseActivity
import com.jie.databinding.databinding.ActivityMainBinding
import com.jie.databinding.interf.ItemClickListener

/**
 *  封装DataBinding
 *  https://juejin.cn/post/6957608813809795108
 */
class MainActivity : BaseActivity<ActivityMainBinding>() {
    lateinit var mHomeAdapter: HomeAdapter
    private val itemClickListener = object : ItemClickListener<String> {
        override fun onItemClick(view: View, position: Int, data: String) {
            Log.d("onItemClick", "wyj data:$data   position:${mHomeAdapter.getActualPosition(data)}")
        }
    }

    override fun ActivityMainBinding.initBinding() {
        mHomeAdapter = HomeAdapter(itemClickListener)
        with(recyclerView) {
            layoutManager = LinearLayoutManager(this@MainActivity).apply {
                orientation = RecyclerView.VERTICAL
            }
            adapter = mHomeAdapter
        }
        mHomeAdapter.setData(listOf("a","b","c","d","e","f"))
        btn.setOnClickListener {
            Log.d("刷新", "initBinding: wyj 再次设置数据")
            mHomeAdapter.setData(listOf("c","d","e","f"))
        }
    }
}