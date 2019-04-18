package com.jayner.githubrepos.trending

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jayner.githubrepos.data.GitHubRepository
import com.jayner.githubrepos.idlingresource.EspressoTestingIdlingResource
import org.androidannotations.annotations.Bean
import org.androidannotations.annotations.EBean

/**
 * Factory for creating a [TrendingReposViewModel] with a constructor that takes a [GitHubRepository].
 */
@EBean(scope = EBean.Scope.Singleton)
class TrendingReposViewModelFactory: ViewModelProvider.Factory {

    @Bean
    lateinit var gitHubRepository: GitHubRepository

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TrendingReposViewModel(gitHubRepository, EspressoTestingIdlingResource.getInstance()) as T
    }

}