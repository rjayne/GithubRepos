package com.jayner.githubrepos.data

import android.content.Context
import android.util.Log
import com.google.gson.GsonBuilder
import com.jayner.githubrepos.BuildConfig
import okhttp3.Cache
import okhttp3.OkHttpClient
import org.androidannotations.annotations.EBean
import org.androidannotations.annotations.RootContext
import org.threeten.bp.ZonedDateTime
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

@EBean(scope = EBean.Scope.Singleton)
class RestServiceAccessor {

    @RootContext
    lateinit var context: Context

    lateinit var gitHubRestClient: GitHubRestClient

    init {
        gitHubRestClient = provideGithubRestService()
    }

    internal fun provideRetrofit(baseUrl: String): Retrofit {
        val gson = GsonBuilder()
            .registerTypeAdapter(ZonedDateTime::class.java, DateTimeTypeConverter())
            .create()
        val callFactory = OkHttpClient.Builder().build()

        val httpCacheDir = File(context.getCacheDir(), "HttpResponseCache")
        Log.d(TAG, "initOkHttpClient - cache dir: " + context.getCacheDir())

        var httpClient = OkHttpClient.Builder()
            .cache(Cache(httpCacheDir, HTTP_RESPONSE_DISK_CACHE_MAX_SIZE))
            .retryOnConnectionFailure(true)
            .build()
        return Retrofit.Builder()
            .callFactory(callFactory)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .baseUrl(baseUrl)
            .build()
    }

    private fun provideGithubRestService(): GitHubRestClient {
        val retrofit = provideRetrofit(BuildConfig.API_URL)
        return retrofit.create(GitHubRestClient::class.java)
    }

    companion object {
        private val TAG = "RestServiceAccessor"
        private val HTTP_RESPONSE_DISK_CACHE_MAX_SIZE = (10 * 1024 * 1024).toLong() // 10 MiB
        private val READ_TIMEOUT = 60000 // 1 min in ms
        private val CONNECT_TIMEOUT = 60000
    }
}