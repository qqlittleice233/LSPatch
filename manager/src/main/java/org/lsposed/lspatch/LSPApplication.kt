package org.lsposed.lspatch

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.lsposed.hiddenapibypass.HiddenApiBypass
import org.lsposed.lspatch.manager.AppBroadcastReceiver
import org.lsposed.lspatch.manager.ModuleService
import org.lsposed.lspatch.util.LSPPackageManager
import org.lsposed.lspatch.util.ShizukuApi
import java.io.File

lateinit var lspApp: LSPApplication

class LSPApplication : Application() {

    lateinit var prefs: SharedPreferences
    lateinit var tmpApkDir: File

    val globalScope = CoroutineScope(Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        HiddenApiBypass.addHiddenApiExemptions("")
        lspApp = this
        filesDir.mkdir()
        tmpApkDir = cacheDir.resolve("apk").also { it.mkdir() }
        prefs = lspApp.getSharedPreferences("settings", Context.MODE_PRIVATE)
        ShizukuApi.init()
        AppBroadcastReceiver.register(this)
        startService(Intent(this, ModuleService::class.java))
        globalScope.launch { LSPPackageManager.fetchAppList() }
    }
}
