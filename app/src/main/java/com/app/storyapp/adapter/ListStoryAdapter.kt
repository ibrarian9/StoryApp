package com.app.storyapp.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.storyapp.DetailStoryActivity
import com.app.storyapp.databinding.ListLayoutBinding
import com.app.storyapp.models.ListStoryItem
import com.squareup.picasso.Picasso

class ListStoryAdapter : RecyclerView.Adapter<ListStoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val bind = ListLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(bind)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int = differ.currentList.size

    inner class ViewHolder(private val bind: ListLayoutBinding) : RecyclerView.ViewHolder(bind.root) {
        fun bind(item: ListStoryItem) {
            bind.apply {
                judulStory.text = item.name
                textStory.text = item.description
                Picasso.get().load(item.photoUrl).fit().into(storyPoto)

                itemView.setOnClickListener {
                    val i = Intent(it.context, DetailStoryActivity::class.java)
                    i.putExtra("id", item.id)
                    it.context.startActivity(i, ActivityOptionsCompat.makeSceneTransitionAnimation(it.context as Activity).toBundle())
                }
            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<ListStoryItem>() {
        override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)
}