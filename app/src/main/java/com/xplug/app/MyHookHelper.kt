package com.xplug.app

import android.app.Instrumentation
import android.content.Context
import dalvik.system.DexClassLoader
import java.util.*
import java.lang.reflect.AccessibleObject.setAccessible
import java.lang.reflect.Array


object MyHookHelper {
    /**
     * 加载插件
     */
    fun inject(loader:DexClassLoader){
        var pathLoader = AppContext.context.classLoader
        try {
            var hostPathList = getPathList(pathLoader)
            var plugPathList = getPathList(loader)
            var newElements = combindArray(getDexElements(hostPathList), getDexElements(plugPathList))
            setField(hostPathList,hostPathList.javaClass,"dexElements",newElements)
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun getPathList(baseDexClassLoader:Any):Any{
        return getField(baseDexClassLoader, Class.forName("dalvik.system.BaseDexClassLoader"),"pathList")
    }

    private fun getField(obj:Any,cl:Class<*>,field: String):Any{
        var localField = cl.getDeclaredField(field)
        localField.isAccessible=true
        return localField.get(obj)
    }

    private fun getDexElements(paramObject:Any):Any{
        return getField(paramObject as Object,paramObject::class.java,"dexElements")
    }

    private fun setField(obj:Any,cl:Class<*>,field:String,value:Any){
        var localField = cl.getDeclaredField(field)
        localField.isAccessible = true
        localField.set(obj,value)
    }

    private fun combindArray(host:Any,plug:Any):Any{
        var hostClass = host::class.java.componentType
        var hostLength = java.lang.reflect.Array.getLength(host)
        var allLength = hostLength+java.lang.reflect.Array.getLength(plug)
        var result = Array.newInstance(hostClass,allLength)
        for(index in 0..allLength){
            if(index<hostLength){
                Array.set(result,index,Array.get(host,index))
            }else{
                Array.set(result,index,Array.get(plug,index-hostLength))
            }
        }
        return result
    }

    fun hookActivityResource(context: Context){
        try {
            var activityThreadClass = Class.forName("android.app.ActivityThread")
            var currentActivityThreadMethod = activityThreadClass.getDeclaredMethod("currentActivityThread")
            currentActivityThreadMethod.isAccessible = true
            var currentActivityThread = currentActivityThreadMethod.invoke(null)
            var mInstrumentationField = activityThreadClass.getDeclaredField("mInstrumentation")
            mInstrumentationField.isAccessible = true
            var baseInstrumentation = mInstrumentationField.get(currentActivityThread) as Instrumentation
            var proxy = MyInstrumentation(baseInstrumentation,context)
            mInstrumentationField.set(currentActivityThread,proxy)
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
}