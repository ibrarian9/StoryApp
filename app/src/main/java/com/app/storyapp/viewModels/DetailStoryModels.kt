package com.app.storyapp.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.app.storyapp.data.UserRepository
import com.app.storyapp.models.UserModel

class DetailStoryModels(private val repo: UserRepository): ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return repo.getSession().asLiveData()
    }
}