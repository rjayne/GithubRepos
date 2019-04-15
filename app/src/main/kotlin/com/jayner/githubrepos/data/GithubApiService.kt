package com.jayner.githubrepos.data

import com.jayner.githubrepos.model.Contributor
import com.jayner.githubrepos.model.Repo
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.androidannotations.annotations.AfterInject
import org.androidannotations.annotations.Bean
import org.androidannotations.annotations.EBean

@EBean(scope = EBean.Scope.Singleton)
class GithubApiService {

    @Bean
    lateinit var restServiceAccessor: RestServiceAccessor

    lateinit var githubRestClient: GithubRestClient

    @AfterInject
    fun initAfterInject() {
        githubRestClient = restServiceAccessor.githubRestClient
    }

    fun getTrendingRepos(): Single<List<Repo>> {
        return githubRestClient.getTrendingRepos()
            .map({it.items})
    }

    internal fun getRepo(repoOwner: String, repoName: String): Single<Repo> {
        return githubRestClient.getRepo(repoOwner, repoName)
    }

    internal fun getContributors(url: String): Single<List<Contributor>> {
        return githubRestClient.getContributors(url)
    }

}