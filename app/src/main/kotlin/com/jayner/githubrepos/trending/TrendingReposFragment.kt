package com.jayner.githubrepos.trending

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.jayner.githubrepos.R
import com.jayner.githubrepos.databinding.FragmentTrendingReposBinding
import org.androidannotations.annotations.*

@DataBound
@EFragment(R.layout.fragment_trending_repos)
class TrendingReposFragment: Fragment() {

    @Bean
    lateinit var reposViewModelFactory: TrendingReposViewModelFactory

    @BindingObject
    lateinit var binding: FragmentTrendingReposBinding

    lateinit var trendingReposViewModel: TrendingReposViewModel

    @AfterViews
    fun initUI() {
        Log.d(TAG, "bindData")
        binding.viewModel = trendingReposViewModel

        val adapter = RepoListAdapter()
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated")
        trendingReposViewModel = ViewModelProviders.of(this, reposViewModelFactory).get(TrendingReposViewModel::class.java)
    }

    companion object {
        val TAG = "TrendingReposFragment"
    }
}