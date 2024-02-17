package com.wyj.view.window

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.WindowManager
import com.wyj.view.R


class WindowStudyActivity : AppCompatActivity(), View.OnClickListener {
    companion object {
        const val TAG = "WindowStudyActivity"
        const val REQUEST_CODE_MANAGE_OVERLAY_PERMISSION = 100
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_window_study)
        findViewById<View>(R.id.btn_main_anim).setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        val viewId = v?.id
        when (viewId) {
            R.id.btn_main_anim -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Settings.canDrawOverlays(this)) {
                        showWindow()
                    } else {
                        val uri: Uri = Uri.parse("package:$packageName")
                        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, uri)
                        startActivityForResult(intent, REQUEST_CODE_MANAGE_OVERLAY_PERMISSION)
                    }
                }
            }
        }
    }

    private fun showWindow() {
        EasyWindow.with(application)
            .setContentView(R.layout.window_hint)
            .addWindowFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            .setImageDrawable(android.R.id.icon, R.drawable.ic_dialog_tip_finish)
            .setText(android.R.id.message, "hello WindowManager")
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_MANAGE_OVERLAY_PERMISSION) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this)) {
                showWindow()
            } else {
                Log.d(TAG, "onActivityResult: not grant manage overlay permission.")
            }
        }
    }
}