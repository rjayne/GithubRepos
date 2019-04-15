package com.jayner.githubrepos.trending

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import org.androidannotations.annotations.Bean
import org.androidannotations.annotations.EFragment

//@EFragment
class TrendingReposFragment: Fragment() {

//    @Bean
    lateinit var viewModelFactory: TrendingViewModelFactory

    lateinit var trendingReposViewModel: TrendingReposViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        trendingReposViewModel = ViewModelProviders.of(this, viewModelFactory).get(TrendingReposViewModel::class.java)
    }
}