package com.jayner.githubrepos.repodetails

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jayner.githubrepos.data.GitHubRepository
import com.jayner.githubrepos.model.Contributor
import com.jayner.githubrepos.model.Repo
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class RepoDetailsViewModel(val gitHubRepository: GitHubRepository): ViewModel() {

    private val repo = MutableLiveData<Repo>()
    private val repoLoadError = MutableLiveData<Boolean>()
    private val loading = MutableLiveData<Boolean>()

    private val contributors = MutableLiveData<List<Contributor>>()
    private val contributorsLoadError = MutableLiveData<Boolean>()
    private val contributorsLoading = MutableLiveData<Boolean>()

    private var disposables = CompositeDisposable()

    fun setRepo(selectedRepo: Repo) {
        repo.value = selectedRepo
        contributors.value = emptyList()
        selectedRepo.contributorsUrl?.let{
            fetchRepoContributors(it)
        }
    }

    fun getRepo(): LiveData<Repo> {
        return repo
    }

    fun hasError(): LiveData<Boolean> {
        return repoLoadError
    }

    fun isLoading(): LiveData<Boolean> {
        return loading
    }

    fun getContributors(): LiveData<List<Contributor>> {
        return contributors
    }

    fun hasContributorsError(): LiveData<Boolean> {
        return contributorsLoadError
    }

    fun isContributorsLoading(): LiveData<Boolean> {
        return contributorsLoading
    }

    fun saveToBundle(outState: Bundle) {
        repo.value?.let{
            outState.putStringArray("repo_details", arrayOf(it.owner.login, it.name))
        }
    }

    // Restore if no repo is set already
    fun restoreFromBundle(savedInstanceState: Bundle?) {
        if (repo.value == null) {
            savedInstanceState?.getStringArray("repo_details")?.let{
                fetchRepo(it)
            }
        }
    }

    private fun fetchRepo(repoDetails: Array<String>) {
        loading.value = true
        gitHubRepository.getRepo(repoDetails[0], repoDetails[1])
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<Repo> {
                override fun onSubscribe(d: Disposable) {
                    disposables.add(d)
                }

                override fun onSuccess(repoFound: Repo) {
                    repoLoadError.value = false
                    repo.value = repoFound
                    loading.value = false
                    repoFound.contributorsUrl?.let{
                        fetchRepoContributors(it)
                    }
                }

                override fun onError(t: Throwable) {
                    Log.e(TAG, "Error loading repo", t)
                    repoLoadError.value = true
                    loading.value = false
                }
            })
    }

    private fun fetchRepoContributors(url: String) {
        contributorsLoading.value = true
        contributorsLoadError.value = false
        gitHubRepository.getContributors(url)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<List<Contributor>> {
                override fun onSubscribe(d: Disposable) {
                    disposables.add(d)
                }

                override fun onSuccess(contributorsFound: List<Contributor>) {
                    contributorsLoadError.value = false
                    contributors.value = contributorsFound
                    contributorsLoading.value = false
                }

                override fun onError(t: Throwable) {
                    Log.e(TAG, "Error loading contributors", t)
                    contributorsLoadError.value = true
                    contributorsLoading.value = false
                }
            })
    }

    override fun onCleared() {
        disposables?.dispose()
    }

    companion object {
        val TAG = "RepoDetailsViewModel"
    }
}