package com.jayner.githubrepos.data

import com.jayner.githubrepos.model.Contributor
import com.jayner.githubrepos.model.Repo
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.androidannotations.annotations.Bean
import org.androidannotations.annotations.EBean
import java.util.*

@EBean(scope = EBean.Scope.Singleton)
class GitHubRepository() {

    @Bean
    lateinit var gitHubApiService: GitHubApiService

    private val cachedTrendingRepos = ArrayList<Repo>()
    private val cachedContributors = HashMap<String, List<Contributor>>()
    var scheduler = Schedulers.io()

    fun getTrendingRepos(): Single<List<Repo>> {
//        var cachedObservable = cachedTrendingRepos().toObservable()
//        var apiObservable = apiTrendingRepos().toObservable()

//        return Observable.concat(cachedObservable, apiObservable).firstElement().subscribeOn(scheduler).toSingle()

        return Maybe.concat(cachedTrendingRepos(), apiTrendingRepos())
            .firstOrError()
            .subscribeOn(scheduler)
    }

    fun getRepo(repoOwner: String, repoName: String): Single<Repo> {
        return Maybe.concat(cachedRepo(repoOwner, repoName), apiRepo(repoOwner, repoName))
            .firstOrError()
            .subscribeOn(scheduler)
    }

    fun getContributors(url: String): Single<List<Contributor>> {
        return Maybe.concat(cachedContributors(url), apiContributors(url))
            .firstOrError()
            .subscribeOn(scheduler)
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