package com.jayner.githubrepos

import android.app.Application
import android.content.Context
import android.net.http.HttpResponseCache
import android.util.Log
import org.androidannotations.annotations.EApplication
import java.io.File
import java.io.IOException

@EApplication
class GitHubReposApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        initializeApplication(applicationContext)
    }

    fun initializeApplication(context: Context) {
        setupHttpCache(context)
    }

    private fun setupHttpCache(context: Context) {
        val cache = HttpResponseCache.getInstalled()
        if (cache == null) {
            installHttpCache(context)
        }
    }

    // Enabling caching of all of the application's HTTP requests
    private fun installHttpCache(context: Context) {
        Log.d(TAG, "installing HttpCache")
        try {
            val httpCacheDir = File(context.cacheDir, "http") // the files are deleted when the app is uninstalled
            val httpCacheSize = (10 * 1024 * 1024).toLong() // 10 MiB
            HttpResponseCache.install(httpCacheDir, httpCacheSize)
        } catch (e: IOException) {
            Log.i(TAG, "HTTP response cache installation failed:$e")
        }
    }

    companion object {
        val TAG = "GitHubReposApplication"
    }
}