package com.wyj.memory

import android.app.Application
import android.content.pm.PackageManager
import android.util.Log
import leakcanary.AppWatcher
import leakcanary.LeakCanary

class MyApplication : Application() {
    companion object {
        private const val TAG = "MyApplication"
    }
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate: wyj")
        leakCanaryConfig()
        // 获取已经启动的ContentProvider的name，以及initOrder
        packageManager
            .getPackageInfo(packageName, PackageManager.GET_PROVIDERS)
            .providers
            .forEach {
                Log.d(TAG, "${it.name}: ${it.initOrder}")
            }
    }

    private fun leakCanaryConfig() {
        LeakCanary.Config().copy(retainedVisibleThreshold = 5)
        LeakCanary.showLeakDisplayActivityLauncherIcon(true)
    }
}