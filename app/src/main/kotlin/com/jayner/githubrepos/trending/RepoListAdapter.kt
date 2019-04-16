package com.jayner.githubrepos.trending

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.jayner.githubrepos.R
import com.jayner.githubrepos.databinding.ItemGithubRepoBinding
import com.jayner.githubrepos.model.Repo

class RepoListAdapter(val repoSelectedListener: RepoSelectedListener): RecyclerView.Adapter<RepoListAdapter.RepoViewHolder>() {
    
    private val gitHubRepos = ArrayList<Repo>()

    init {
        setHasStableIds(true)
    }

    fun setData(repos: List<Repo>) {
        gitHubRepos.clear()
        gitHubRepos.addAll(repos)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ItemGithubRepoBinding>(layoutInflater,
            R.layout.item_github_repo, parent, false)
        return RepoViewHolder(binding, repoSelectedListener)
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

    class RepoViewHolder(val binding: ItemGithubRepoBinding, val repoSelectedListener: RepoSelectedListener) : RecyclerView.ViewHolder(binding.root) {

        private var repo: Repo? = null

        init {
            itemView.setOnClickListener { v ->
                repo?.let {
                    repoSelectedListener.onRepoSelected(it)
                }
            }
        }

        fun bind(repoToBind: Repo) {
            repo = repoToBind

            with(binding) {
                repo = repoToBind
                executePendingBindings()
            }
        }

    }
}