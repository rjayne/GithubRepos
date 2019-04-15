package com.jayner.githubrepos.data

import com.jayner.githubrepos.BuildConfig
import okhttp3.OkHttpClient
import org.androidannotations.annotations.EBean
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@EBean(scope = EBean.Scope.Singleton)
class RestServiceAccessor {

    lateinit var gitHubRestClient: GitHubRestClient

    init {
        gitHubRestClient = provideGithubRestService()
    }

    internal fun provideRetrofit(baseUrl: String): Retrofit {
        val callFactory = OkHttpClient.Builder().build()
        return Retrofit.Builder()
            .callFactory(callFactory)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .baseUrl(baseUrl)
            .build()
    }

    private fun provideGithubRestService(): GitHubRestClient {
        val retrofit = provideRetrofit(BuildConfig.API_URL)
        return retrofit.create(GitHubRestClient::class.java)
    }

}