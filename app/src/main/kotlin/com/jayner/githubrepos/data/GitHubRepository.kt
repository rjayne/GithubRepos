package com.jayner.githubrepos.data

import com.jayner.githubrepos.model.Contributor
import com.jayner.githubrepos.model.Repo
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.androidannotations.annotations.Bean
import org.androidannotations.annotations.EBean
import java.util.*

/**
 * This repository is responsible for retrieving the relevant GitHub Repository data from the API and caching it once
 * retrieved.
 *
 * When a call to retrieve the Trending Repos is made to the API, the trending repository data is cached. For subsequent
 * individual repo lookups, the cached trending repos list is first accessed to retrieve the repo. In the case, where
 * the repo is not in that list (ie. GC was performed by the OS), a call to the API is made to retrieve the relevant
 * repo.
 *
 * API lookups are performed to retrieve the repo contributors, with the data cached upon retrieval for subsequent
 * lookup requests.
 */
@EBean(scope = EBean.Scope.Singleton)
class GitHubRepository() {

    @Bean
    lateinit var gitHubApiService: GitHubApiService

    private val cachedTrendingRepos = ArrayList<Repo>()
    private val cachedContributors = HashMap<String, List<Contributor>>()

    fun getTrendingRepos(): Single<List<Repo>> {
        return Maybe.concat(cachedTrendingRepos(), apiTrendingRepos())
            .firstOrError()
            .subscribeOn(Schedulers.io())
    }

    fun getRepo(repoOwner: String, repoName: String): Single<Repo> {
        return Maybe.concat(cachedRepo(repoOwner, repoName), apiRepo(repoOwner, repoName))
            .firstOrError()
            .subscribeOn(Schedulers.io())
    }

    fun getContributors(url: String): Single<List<Contributor>> {
        return Maybe.concat(cachedContributors(url), apiContributors(url))
            .firstOrError()
            .subscribeOn(Schedulers.io())
    }

    fun clearCache() {
        cachedTrendingRepos.clear()
        cachedContributors.clear()
    }

    private fun cachedContributors(url: String): Maybe<List<Contributor>> {
        return Maybe.create { e ->
            cachedContributors[url]?.let{
                e.onSuccess(it)
            }
            e.onComplete()
        }
    }

    private fun apiContributors(url: String): Maybe<List<Contributor>> {
        return gitHubApiService.getContributors(url)
            .doOnSuccess({ contributors -> cachedContributors.put(url, contributors) })
            .toMaybe()
    }

    private fun cachedRepo(repoOwner: String, repoName: String): Maybe<Repo> {
        return Maybe.create { e ->
            for (cachedRepo in cachedTrendingRepos) {
                if (cachedRepo.owner.login.equals(repoOwner) && cachedRepo.name.equals(repoName)) {
                    e.onSuccess(cachedRepo)
                    break
                }
            }
            e.onComplete()
        }
    }

    private fun apiRepo(repoOwner: String, repoName: String): Maybe<Repo> {
        return gitHubApiService.getRepo(repoOwner, repoName).toMaybe()
    }

    private fun cachedTrendingRepos(): Maybe<List<Repo>> {
        return Maybe.create { e ->
            if (!cachedTrendingRepos.isEmpty()) {
                e.onSuccess(cachedTrendingRepos)
            }

            e.onComplete()
        }
    }

    private fun apiTrendingRepos(): Maybe<List<Repo>> {
        return gitHubApiService.getTrendingRepos()
            .doOnSuccess({ repos ->
                cachedTrendingRepos.clear()
                cachedTrendingRepos.addAll(repos)
            })
            .toMaybe()
    }
}