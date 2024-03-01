package com.app.storyapp.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import com.app.storyapp.data.StoryRepository
import com.app.storyapp.models.ListStoryItem

class MapStoryModels(private val repo: StoryRepository): ViewModel() {
    fun getStory(): LiveData<List<ListStoryItem>> {
        return repo.getStory().asFlow().asLiveData()
    }
}