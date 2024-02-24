package com.app.storyapp.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.app.storyapp.data.UserRepository
import com.app.storyapp.models.UserModel
import kotlinx.coroutines.launch

class ListStoryModels(private val repo: UserRepository): ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return repo.getSession().asLiveData()
    }
    fun logout() {
        viewModelScope.launch {
            repo.logout()
        }
    }
}