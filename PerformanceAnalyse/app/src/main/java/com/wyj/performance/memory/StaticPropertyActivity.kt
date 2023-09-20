package com.wyj.performance.memory

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.wyj.performance.R

class StaticPropertyActivity : AppCompatActivity(), CallBack {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_static_property)
        val iv: ImageView = findViewById(R.id.iv)
        val bitmap = BitmapFactory.decodeResource(resources, R.mipmap.logo)
        iv.setImageBitmap(bitmap)
        CallBackManager.addCallBack(this)
    }

    override fun dpOperate() {
    }

    override fun onDestroy() {
        super.onDestroy()
//        CallBackManager.removeCallBack(this)
    }
}