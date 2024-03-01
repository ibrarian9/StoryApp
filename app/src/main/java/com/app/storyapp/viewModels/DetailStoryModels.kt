package com.app.storyapp.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.app.storyapp.data.StoryRepository
import com.app.storyapp.models.Story
import com.app.storyapp.models.UserModel

class DetailStoryModels(private val repo: StoryRepository): ViewModel() {

    suspend fun getDetailStory(id: String): LiveData<Story> {
        return repo.getDetailStory(id).asLiveData()
    }
}