package com.jayner.githubrepos.trending

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jayner.githubrepos.data.GitHubRepository
import com.jayner.githubrepos.model.Repo
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable

class TrendingReposViewModel(val gitHubRepository: GitHubRepository): ViewModel() {

    private val repos = MutableLiveData<List<Repo>>()
    private val repoLoadError = MutableLiveData<Boolean>()
    private val loading = MutableLiveData<Boolean>()

    private var disposable: Disposable? = null

    init {
        fetchRepos()
    }

    fun getRepos(): LiveData<List<Repo>> {
        return repos
    }

    fun hasError(): LiveData<Boolean> {
        return repoLoadError
    }

    fun isLoading(): LiveData<Boolean> {
        return loading
    }

    private fun fetchRepos() {
        loading.value = true
        gitHubRepository.getTrendingRepos()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<List<Repo>> {
                override fun onSubscribe(d: Disposable) {
                    disposable = d
                }

                override fun onSuccess(trendingRepos: List<Repo>) {
                    repoLoadError.value = false
                    repos.value = trendingRepos
                    loading.value = false
                }

                override fun onError(t: Throwable) {
                    Log.e(TAG, "Error loading repos - ${t.message}", t)
                    repoLoadError.value = true
                    loading.value = false
                }
            })
    }

    override fun onCleared() {
        disposable?.dispose()
    }

    companion object {
        private val TAG = "TrendingReposViewModel"
    }

}