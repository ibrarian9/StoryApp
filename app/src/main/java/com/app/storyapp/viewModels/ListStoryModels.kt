package com.app.storyapp.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.app.storyapp.data.StoryRepository
import com.app.storyapp.models.ListStoryItem
import com.app.storyapp.models.UserModel
import kotlinx.coroutines.launch

class ListStoryModels(private val repo: StoryRepository): ViewModel() {

    fun getSession(): LiveData<UserModel> {
        return repo.getSession().asLiveData()
    }
    fun logout() {
        viewModelScope.launch {
            repo.logout()
        }
    }
    fun getAllStory(): LiveData<PagingData<ListStoryItem>> {
        return repo.getAllStory().cachedIn(viewModelScope)
    }
}