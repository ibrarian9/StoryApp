package com.app.storyapp

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.app.storyapp.databinding.ActivityDetailStoryBinding
import com.app.storyapp.models.Story
import com.app.storyapp.viewModels.DetailStoryModels
import com.app.storyapp.viewModels.ViewModelsFactory
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch

class DetailStoryActivity : AppCompatActivity() {

    private val detailStoryModels by viewModels<DetailStoryModels> {
        ViewModelsFactory.getInstance(this)
    }

    private lateinit var bind: ActivityDetailStoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(bind.root)

        lifecycleScope.launch {
            getDataFromIntent()
        }
    }

    private suspend fun getDataFromIntent() {
        val dataId = intent.getStringExtra("id")

        if (dataId != null) {
            detailStoryModels.getDetailStory(dataId).observe(this){
                fetchDataToBind(it)
            }
        }
    }

    private fun fetchDataToBind(data: Story?) {
        if (data != null){
            bind.storyJudul.text = data.name
            Picasso.get().load(data.photoUrl).fit().into(bind.storyPoto)
            bind.storyDesc.text = data.description
        }
    }
}