package com.app.storyapp.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.storyapp.data.UserRepository
import com.app.storyapp.models.UserModel
import kotlinx.coroutines.launch

class LoginViewModels(private val repository: UserRepository): ViewModel() {
    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }
}