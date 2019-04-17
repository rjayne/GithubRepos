package com.jayner.githubrepos.trending

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.jayner.githubrepos.R
import com.jayner.githubrepos.databinding.FragmentTrendingReposBinding
import com.jayner.githubrepos.model.Repo
import com.jayner.githubrepos.repodetails.RepoDetailsFragment_
import com.jayner.githubrepos.repodetails.RepoDetailsViewModel
import com.jayner.githubrepos.repodetails.RepoDetailsViewModelFactory
import org.androidannotations.annotations.*

@DataBound
@EFragment(R.layout.fragment_trending_repos)
class TrendingReposFragment: Fragment(), RepoSelectedListener {

    @Bean
    lateinit var reposViewModelFactory: TrendingReposViewModelFactory

    @Bean
    lateinit var repoDetailsViewModelFactory: RepoDetailsViewModelFactory

    @BindingObject
    lateinit var binding: FragmentTrendingReposBinding

    lateinit var trendingReposViewModel: TrendingReposViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        trendingReposViewModel = ViewModelProviders.of(activity as FragmentActivity, reposViewModelFactory).get(TrendingReposViewModel::class.java)
        trendingReposViewModel.start()
    }

    @AfterViews
    fun initUI() {
        binding.viewModel = trendingReposViewModel

        val adapter = RepoListAdapter(this)
        binding.repoList.adapter = adapter
        binding.setLifecycleOwner(viewLifecycleOwner)

        subscribeUi(adapter)
    }

    private fun subscribeUi(adapter: RepoListAdapter) {
        trendingReposViewModel.isLoading().observe(viewLifecycleOwner, Observer { isLoading ->
            binding.isLoading = isLoading
        })

        trendingReposViewModel.hasError().observe(viewLifecycleOwner, Observer { hasError ->
            binding.hasError = hasError
        })

        trendingReposViewModel.getRepos().observe(viewLifecycleOwner, Observer { repos ->
            adapter.setData(repos)
        })
    }

    override fun onRepoSelected(repo: Repo) {
        val selectedRepoViewModel = ViewModelProviders.of(activity as FragmentActivity, repoDetailsViewModelFactory).get(RepoDetailsViewModel::class.java)
        selectedRepoViewModel.setRepo(repo)
        (activity as FragmentActivity).getSupportFragmentManager().beginTransaction()
            .replace(R.id.content_frame, RepoDetailsFragment_())
            .addToBackStack(null)
            .commit()
    }

    companion object {
        val TAG = "TrendingReposFragment"
    }
}