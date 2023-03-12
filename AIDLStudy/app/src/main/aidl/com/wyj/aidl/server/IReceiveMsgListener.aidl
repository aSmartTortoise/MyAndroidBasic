package com.wyj.aidl.server;
import com.wyj.aidl.server.Msg;

interface IReceiveMsgListener {
    void onReceive(in Msg msg);
}