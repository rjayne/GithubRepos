package com.jayner.githubrepos.repodetails

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.jayner.githubrepos.R
import com.jayner.githubrepos.databinding.FragmentRepoDetailsBinding
import org.androidannotations.annotations.*

@DataBound
@EFragment(R.layout.fragment_repo_details)
class RepoDetailsFragment: Fragment() {

    @Bean
    lateinit var repoDetailsViewModelFactory: RepoDetailsViewModelFactory

    @BindingObject
    lateinit var binding: FragmentRepoDetailsBinding

    lateinit var repoDetailsViewModel: RepoDetailsViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated")
        repoDetailsViewModel = ViewModelProviders.of(activity as FragmentActivity, repoDetailsViewModelFactory).get(RepoDetailsViewModel::class.java)

        repoDetailsViewModel.restoreFromBundle(savedInstanceState)
    }

    @AfterViews
    fun initUI() {
        Log.d(TAG, "initUI")
        binding.viewModel = repoDetailsViewModel

        val adapter = RepoContributorListAdapter()
        binding.contributorList.adapter = adapter
        binding.setLifecycleOwner(viewLifecycleOwner)

        subscribeUi(adapter)
    }

    private fun subscribeUi(adapter: RepoContributorListAdapter) {
        repoDetailsViewModel.isLoading().observe(viewLifecycleOwner, Observer { isLoading ->
            binding.isLoading = isLoading
        })

        repoDetailsViewModel.hasError().observe(viewLifecycleOwner, Observer { hasError ->
            binding.hasError = hasError
        })

        repoDetailsViewModel.getRepo().observe(viewLifecycleOwner, Observer { repo ->
            binding.repo = repo
        })

        repoDetailsViewModel.isContributorsLoading().observe(viewLifecycleOwner, Observer { isLoading ->
            binding.isContributorsLoading = isLoading
        })

        repoDetailsViewModel.hasContributorsError().observe(viewLifecycleOwner, Observer { hasError ->
            binding.hasContributorsError = hasError
        })

        repoDetailsViewModel.getContributors().observe(viewLifecycleOwner, Observer { contributors ->
            adapter.setData(contributors)
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        repoDetailsViewModel.saveToBundle(outState)
    }

    companion object {
        val TAG = "RepoDetailsFragment"
    }
}