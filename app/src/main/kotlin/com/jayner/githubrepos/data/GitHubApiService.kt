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
            .map({it.items})
    }

    fun getRepo(repoOwner: String, repoName: String): Single<Repo> {
        Log.d(TAG, "getRepo")
        return gitHubRestClient.getRepo(repoOwner, repoName)
    }

    /**
     * Retrieve non-anonymous contributors at the specified contributors URL.
     * Note: only the first 500 author email addresses in the repository link to GitHub users. The rest will appear
     * as anonymous contributors without associated GitHub user information. We do not want to display these.
     */
    fun getContributors(url: String): Single<List<Contributor>> {
        return gitHubRestClient.getContributors(url+PARAM_NON_ANON_USERS)
    }

    companion object {
        val TAG = "GitHubApiService"
        val PARAM_NON_ANON_USERS = "?q=anon:false"
    }
}