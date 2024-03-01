package com.app.storyapp.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.storyapp.data.StoryRepository
import com.app.storyapp.models.UserModel
import kotlinx.coroutines.launch

class LoginViewModels(private val repository: StoryRepository): ViewModel() {
    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }
}