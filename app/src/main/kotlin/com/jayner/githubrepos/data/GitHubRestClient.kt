package com.jayner.githubrepos.data

import com.jayner.githubrepos.model.Contributor
import com.jayner.githubrepos.model.Repo
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url

interface GitHubRestClient {

    @GET("search/repositories?q=language:kotlin&order=desc&sort=stars")
    fun getTrendingRepos(): Single<TrendingReposResponse>

    @GET("repos/{owner}/{name}")
    fun getRepo(@Path("owner") repoOwner: String, @Path("name") repoName: String): Single<Repo>

    @GET
    fun getContributors(@Url url: String): Single<List<Contributor>>

}