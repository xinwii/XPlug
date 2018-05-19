package com.xplug.app

import android.content.Intent
import android.os.Handler
import android.os.Message

class ActivityThreadHandlerCallback(val base:Handler): Handler.Callback {
    override fun handleMessage(msg: Message?): Boolean {
        if(msg!!.what==100){
            handleMessage(msg)
        }
        base.handleMessage(msg)
        return true
    }
    fun handleLaunchActivity(msg:Message){
        var obj = msg.obj
        try {
            var intent = obj.javaClass.getDeclaredField("intent")
            intent.isAccessible = true
            var raw = intent.get(obj) as Intent
            var target = raw.getParcelableExtra("") as Intent
            raw.component = target.component
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
}