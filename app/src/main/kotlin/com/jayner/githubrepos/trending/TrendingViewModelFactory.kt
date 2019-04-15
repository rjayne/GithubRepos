package com.jayner.githubrepos.trending

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jayner.githubrepos.data.GitHubRepository
import org.androidannotations.annotations.Bean
import org.androidannotations.annotations.EBean

/**
 * Class created to enable custom injection into ViewModels for Trending component
 */
@EBean(scope = EBean.Scope.Singleton)
class TrendingViewModelFactory: ViewModelProvider.Factory {

    @Bean
    lateinit var gitHubRepository: GitHubRepository

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        try {
            when(modelClass) {
                TrendingReposViewModel::class -> {
                    return TrendingReposViewModel(gitHubRepository) as T
                }
            }
        } catch (e: Exception) {
            throw RuntimeException("Error creating viewModel for class: ${modelClass.simpleName}", e)
        }

        throw RuntimeException("No ViewModel known for ${modelClass.simpleName}")
    }
}