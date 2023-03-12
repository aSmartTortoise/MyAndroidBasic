package com.wyj.aidl.server;
import com.wyj.aidl.server.Msg;
import com.wyj.aidl.server.IReceiveMsgListener;

interface IMsgManager {
    // 发消息
    void sendMsg(in Msg msg);
      	// 客户端注册监听回调
    void registerReceiveListener(IReceiveMsgListener listener);
      	// 客户端取消监听回调
    void unregisterReceiveListener(IReceiveMsgListener listener);
}