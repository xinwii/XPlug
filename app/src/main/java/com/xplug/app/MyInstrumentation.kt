package com.xplug.app

import android.app.Activity
import android.app.Instrumentation
import android.content.Context
import android.content.res.AssetManager
import android.content.res.Resources
import android.os.Bundle
import android.os.Environment

class MyInstrumentation(val base:Instrumentation,val mContext:Context):Instrumentation() {
    override fun callActivityOnCreate(activity: Activity?, icicle: Bundle?) {
        try{
            var classList = activity!!::class.java
            var baseField = Activity::class.java.superclass.superclass.getDeclaredField("mBase")
            baseField.isAccessible = true
            var contextImplClass = Class.forName("android.app.ContextImpl")
            var resourceField = contextImplClass.getDeclaredField("mResources")
            resourceField.isAccessible = true
            var apkPath = Environment.getExternalStorageDirectory().absolutePath+"/aaa.apk"
            var path = context.applicationContext.packageResourcePath
            var assetManager = AssetManager::class.java.newInstance()
            var addAssetPathMethod = assetManager.javaClass.getDeclaredMethod("addAssetPath",String::class.java)
            addAssetPathMethod.isAccessible = true
            addAssetPathMethod.invoke(assetManager,path)
            addAssetPathMethod.invoke(assetManager,apkPath)
            var ensureStringBlocks = AssetManager::class.java.getDeclaredMethod("ensureStringBlocks")
            ensureStringBlocks.isAccessible = true
            ensureStringBlocks.invoke(assetManager)
            var supResource = context.resources
            var newResource = Resources(assetManager,supResource.displayMetrics,supResource.configuration)
            resourceField.set(baseField,newResource)
        }catch (e:Exception)
        {e.printStackTrace()}
        super.callActivityOnCreate(activity, icicle)
    }
}