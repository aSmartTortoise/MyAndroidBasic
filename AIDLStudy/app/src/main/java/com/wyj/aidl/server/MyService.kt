package com.wyj.aidl.server

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.RemoteCallbackList
import android.os.RemoteException
import android.util.Log
import java.util.*

/**
 *  参考文章：https://juejin.cn/post/7123129439898042376#heading-0
 *
 *  当服务器定义的接口和客户端定义的接口不完全相同，比如服务器定义了方法A，而客户端接口没有定义方法A。
 *      客户端也是能够绑定到服务器的，也是可以通信的。
 */
class MyService : Service() {
    companion object {
        const val TAG = "MyService"
    }
    private val receiveListeners =  RemoteCallbackList<IReceiveMsgListener>()



    override fun onBind(intent: Intent): IBinder {
        Log.d(TAG, "onBind: wyj")
        return MyBinder()
    }

    inner class MyBinder: IMsgManager.Stub() {
        override fun sendMsg(msg: Msg?) {
            val n = receiveListeners.beginBroadcast()
            Log.d(TAG, "sendMsg: wyj msg:$msg, n:$n")
            for (i in 0 until n) {
                receiveListeners.getBroadcastItem(i)?.run {
                    try {
                        val serverMsg = Msg("服务器响应 ${Date(System.currentTimeMillis())}\n ${packageName}",
                            System.currentTimeMillis())
                        onReceive(serverMsg)
                    } catch (e: RemoteException) {
                        e.printStackTrace()
                    }
                }
            }
            receiveListeners.finishBroadcast()
        }

        override fun registerReceiveListener(listener: IReceiveMsgListener?) {
            receiveListeners.register(listener)
        }

        override fun unregisterReceiveListener(listener: IReceiveMsgListener?) {
            val unregister = receiveListeners.unregister(listener)
            if (unregister) {
                Log.d(TAG, "unregisterReceiveListener: 取消订阅成功")
            } else {
                Log.d(TAG, "unregisterReceiveListener: 取消订阅失败")
            }
        }

        override fun sendDelayMessage(msg: Msg?, delayTime: Long) {
            Log.d(TAG, "sendDelayMessage: wyj msg:$msg, delayTime:$delayTime")
        }

    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate: wyj")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand: wyj")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onStart(intent: Intent?, startId: Int) {
        super.onStart(intent, startId)
        Log.d(TAG, "onStart: wyj")
    }

    override fun onUnbind(intent: Intent?): Boolean {
        val result = super.onUnbind(intent)
        Log.d(TAG, "onUnbind: wyj")
        return true
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
        Log.d(TAG, "onRebind: wyj")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: wyj")
    }
}