package com.xplug.app

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.Toast
import dalvik.system.DexClassLoader

class MainActivity : AppCompatActivity() {
    private  val startBtn:Button by lazy {
        findViewById<Button>(R.id.startBtn)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startBtn.setOnClickListener({
            var intent = Intent()
            intent.component = ComponentName("com.xplug.app",
                    "com.xplug.app.NoUseActivity")
            startActivity(intent)
        })
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        object : Thread() {
            override fun run() {
                //创建一个属于我们自己插件的ClassLoader，我们分析过只能使用DexClassLoader
                var cachePath = this@MainActivity.cacheDir.absolutePath
                var apkPath = Environment.getExternalStorageDirectory().absolutePath+"/aaa.apk"
                var classLoader = DexClassLoader(apkPath,cachePath,cachePath, classLoader)
                MyHookHelper.inject(classLoader)
                AMSHookHelper.hookActivityManagerNative()
                AMSHookHelper.hookActivityThreadHandler()
                runOnUiThread {
                    Toast.makeText(this@MainActivity,"加载完成",Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }
}
