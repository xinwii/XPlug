package com.xplug.app

import android.os.Handler
import java.lang.reflect.Proxy
import java.util.*

object AMSHookHelper {
    public var LAUNCH_ACTIVITY:Int?=null
    fun hookActivityManagerNative(){
        var activityManagerNativeClass = Class.forName("android.app.ActivityManagerNative")
        var defaultField = activityManagerNativeClass.getDeclaredField("gDefault")
        defaultField.isAccessible = true
        var defult = defaultField.get(null)
        var singleton = Class.forName("android.util.Singleton")
        var instanceField = singleton.getDeclaredField("mInstance")
        instanceField.isAccessible = true
        var rawIActivityManager = instanceField.get(defult) as Object
        var iActivityManagerInterface = Class.forName("android.app.IActivityManager")
        var proxy = Proxy.newProxyInstance(Thread.currentThread().contextClassLoader, arrayOf(iActivityManagerInterface),IActivityManagerHandler(rawIActivityManager))
        instanceField.set(defult, proxy)
    }
    fun hookActivityThreadHandler(){
        var activityThreadClass = Class.forName("android.app.ActivityThread")
        var currentActivityThreadMethod = activityThreadClass.getDeclaredMethod("currentActivityThread")
        currentActivityThreadMethod.isAccessible = true
        var currentActivityThread = currentActivityThreadMethod.invoke(null)
        var mHField = activityThreadClass.getDeclaredField("mH")
        mHField.isAccessible = true
        var mH = mHField.get(currentActivityThread) as Handler
        var callbackField = Handler::class.java.getDeclaredField("mCallback")
        callbackField.isAccessible = true
        callbackField.set(mH,ActivityThreadHandlerCallback(mH))
    }
}