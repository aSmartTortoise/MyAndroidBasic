package com.wyj.view.window

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.wyj.view.R

class WindowStudyActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_window_study)
        findViewById<View>(R.id.btn_main_anim).setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        val viewId = v?.id
        when (viewId) {
            R.id.btn_main_anim -> {
                EasyWindow.with(application)
                    .setContentView(R.layout.window_hint)
                    .addWindowFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    .setImageDrawable(android.R.id.icon, R.drawable.ic_dialog_tip_finish)
                    .setText(android.R.id.message, "hello WindowManager")
                    .show()

            }
        }
    }
}