package com.jayner.githubrepos.repodetails

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jayner.githubrepos.R
import com.jayner.githubrepos.databinding.ItemContributorBinding
import com.jayner.githubrepos.model.Contributor

class RepoContributorListAdapter: RecyclerView.Adapter<RepoContributorListAdapter.ContributorViewHolder>() {
    
    private var repoContributors = emptyList<Contributor>()

    init {
        setHasStableIds(true)
    }

    fun setData(contributors: List<Contributor>) {
        repoContributors = contributors
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContributorViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ItemContributorBinding>(layoutInflater,
            R.layout.item_contributor, parent, false)
        return ContributorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContributorViewHolder, position: Int) {
        holder.bind(repoContributors[position])
    }

    override fun getItemCount(): Int {
        return repoContributors.size
    }

    override fun getItemId(position: Int): Long {
        return repoContributors[position].id
    }

    class ContributorViewHolder(val binding: ItemContributorBinding) : RecyclerView.ViewHolder(binding.root) {

        private val avatorImageView: ImageView

        init {
            avatorImageView = itemView.findViewById<ImageView>(R.id.image_avatar)
        }

        fun bind(contributorToBind: Contributor) {
            with(binding) {
                contributor = contributorToBind

                avatorImageView.setImageResource(R.drawable.placeholder_avatar)
                if(contributorToBind.avatarUrl != null) {
                    Glide.with(avatorImageView.getContext())
                        .load(contributorToBind.avatarUrl)
                        .placeholder(R.drawable.placeholder_avatar)
                        .centerCrop()
                        .into(avatorImageView)
                }

                executePendingBindings()
            }
        }

    }
}