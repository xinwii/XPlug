package com.xplug.app

import android.content.ComponentName
import android.content.Intent
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.util.*

class IActivityManagerHandler(var base:Object):InvocationHandler {
    override fun invoke(proxy: Any?, method: Method?, args: Array<out Any>?): Any {
        if("startActivity" == method!!.name){
            var raw:Intent
            var index = 0
            for((i,arg) in args!!.withIndex()){
                if(arg is Intent){
                    index = i
                    break
                }
            }
            raw = args[index] as Intent
            var newIntent = Intent()
            var stubPackage = AppContext.context.packageName
            var componentName = ComponentName(stubPackage,MainActivity::class.java.name)
            newIntent.component = componentName
            //todo
            newIntent.putExtra("",raw)
            args[index] = newIntent as Any
            return method.invoke(base,args)
        }
        return method.invoke(base,args)
    }
}