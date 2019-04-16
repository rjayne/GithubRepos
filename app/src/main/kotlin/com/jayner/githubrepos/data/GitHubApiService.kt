package com.jayner.githubrepos.data

import android.util.Log
import com.jayner.githubrepos.model.Contributor
import com.jayner.githubrepos.model.Repo
import io.reactivex.Single
import org.androidannotations.annotations.AfterInject
import org.androidannotations.annotations.Bean
import org.androidannotations.annotations.EBean

@EBean(scope = EBean.Scope.Singleton)
class GitHubApiService {

    @Bean
    lateinit var restServiceAccessor: RestServiceAccessor

    lateinit var gitHubRestClient: GitHubRestClient

    @AfterInject
    fun initAfterInject() {
        gitHubRestClient = restServiceAccessor.gitHubRestClient
    }

    fun getTrendingRepos(): Single<List<Repo>> {
        Log.d(TAG, "getTrendingRepos")
        return gitHubRestClient.getTrendingRepos()
            .map({
                Log.d(TAG, "getTrendingRepos - within map func")
                it.items})
    }

    fun getRepo(repoOwner: String, repoName: String): Single<Repo> {
        return gitHubRestClient.getRepo(repoOwner, repoName)
    }

    fun getContributors(url: String): Single<List<Contributor>> {
        return gitHubRestClient.getContributors(url)
    }

    companion object {
        val TAG = "GitHubApiService"
    }
}