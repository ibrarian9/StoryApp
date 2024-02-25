package com.app.storyapp.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.storyapp.DetailStoryActivity
import com.app.storyapp.R
import com.app.storyapp.models.ListStoryItem
import com.squareup.picasso.Picasso

class ListStoryAdapter(private var listData: MutableList<ListStoryItem>): RecyclerView.Adapter<ListStoryAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_layout, parent, false))
    }

    override fun getItemCount(): Int = listData.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newList: List<ListStoryItem>){
        listData.apply {
            this.clear()
            this.addAll(newList)
            notifyDataSetChanged()
        }
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = listData[position]

        val dataPoto = data.photoUrl
        val dataJudul = data.name
        val dataIsi = data.description

        Picasso.get().load(dataPoto).fit().into(holder.potoStory)
        holder.judulStory.text = dataJudul
        holder.textStory.text = dataIsi

        holder.itemView.setOnClickListener{
            val i = Intent(it.context, DetailStoryActivity::class.java)
            i.putExtra("id", data.id)
            it.context.startActivity(i, ActivityOptionsCompat.makeSceneTransitionAnimation(it.context as Activity).toBundle())
        }
    }

    class MyViewHolder(v: View): RecyclerView.ViewHolder(v) {
        val potoStory: ImageView = v.findViewById(R.id.storyPoto)
        val judulStory: TextView = v.findViewById(R.id.judulStory)
        val textStory: TextView = v.findViewById(R.id.textStory)
    }
}