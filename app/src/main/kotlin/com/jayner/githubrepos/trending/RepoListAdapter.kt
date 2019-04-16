package com.jayner.githubrepos.trending

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.jayner.githubrepos.R
import com.jayner.githubrepos.databinding.ItemGithubRepoBinding
import com.jayner.githubrepos.model.Repo

class RepoListAdapter: RecyclerView.Adapter<RepoListAdapter.RepoViewHolder>() {
    
    private var gitHubRepos = emptyList<Repo>()

    init {
        setHasStableIds(true)
    }

    fun setData(repos: List<Repo>) {
        gitHubRepos = repos
        notifyDataSetChanged() //TODO: Use DiffUtil when we have AutoValue models
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ItemGithubRepoBinding>(layoutInflater,
            R.layout.item_github_repo, parent, false)
        return RepoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RepoViewHolder, position: Int) {
        holder.bind(gitHubRepos[position])
    }

    override fun getItemCount(): Int {
        return gitHubRepos.size
    }

    override fun getItemId(position: Int): Long {
        return gitHubRepos[position].id
    }

    class RepoViewHolder(val binding: ItemGithubRepoBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(repoToBind: Repo) {
            with(binding) {
                repo = repoToBind
                executePendingBindings()
            }
        }

    }
}