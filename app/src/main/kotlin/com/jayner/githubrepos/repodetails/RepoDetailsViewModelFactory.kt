package com.jayner.githubrepos.repodetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jayner.githubrepos.data.GitHubRepository
import org.androidannotations.annotations.Bean
import org.androidannotations.annotations.EBean

/**
 * Factory for creating a [RepoDetailsViewModel] with a constructor that takes a [GitHubRepository].
 */
@EBean(scope = EBean.Scope.Singleton)
class RepoDetailsViewModelFactory: ViewModelProvider.Factory {

    @Bean
    lateinit var gitHubRepository: GitHubRepository

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RepoDetailsViewModel(gitHubRepository) as T
    }

}