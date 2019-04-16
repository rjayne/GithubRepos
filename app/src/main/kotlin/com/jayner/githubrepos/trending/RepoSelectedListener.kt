package com.jayner.githubrepos.trending

import com.jayner.githubrepos.model.Repo

interface RepoSelectedListener {
    fun onRepoSelected(repo: Repo)
}