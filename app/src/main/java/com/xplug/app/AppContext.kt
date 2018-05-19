package com.xplug.app

import android.app.Application
import android.content.Context
import android.content.res.AssetManager
import android.content.res.Resources
import android.os.Environment
import dalvik.system.DexClassLoader

class AppContext :Application(){
    companion object {
        public lateinit var context: Context
    }
    private lateinit var dexClassLoader: DexClassLoader
    private lateinit var assetManager: AssetManager
    private lateinit var mResources: Resources
    private lateinit var mTheme: Resources.Theme
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        context = base!!
        try{
            var apkPath = Environment.getExternalStorageDirectory().absolutePath+"/aaa.apk"
            var path = packageCodePath
            assetManager = AssetManager::class.java.newInstance()
            var addAssetPathMethod = assetManager.javaClass.getDeclaredMethod("addAssetPath",String.javaClass)
            addAssetPathMethod.isAccessible = true
            addAssetPathMethod.invoke(assetManager,apkPath)
            var ensureStringBlocks = AssetManager::class.java.getDeclaredMethod("ensureStringBlocks")
            ensureStringBlocks.isAccessible = true
            ensureStringBlocks.invoke(assetManager)
            var supResource = resources
            mResources = Resources(assetManager,supResource.displayMetrics,supResource.configuration)
            mTheme = mResources.newTheme()
            mTheme.setTo(super.getTheme())
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    override fun getAssets(): AssetManager {
        return assetManager?:super.getAssets()
    }

    override fun getResources(): Resources {
        return mResources?:super.getResources()
    }

    override fun getTheme(): Resources.Theme {
        return mTheme?:super.getTheme()
    }

    fun getContext():Context{
        return context
    }
}