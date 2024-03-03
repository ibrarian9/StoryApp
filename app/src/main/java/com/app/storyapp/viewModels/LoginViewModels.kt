package com.app.storyapp.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import com.app.storyapp.data.StoryRepository
import com.app.storyapp.models.RequestLogin
import com.app.storyapp.models.ResponseLogin
import com.app.storyapp.models.UserModel

class LoginViewModels(private val repo: StoryRepository): ViewModel() {

    fun getSession(): LiveData<UserModel>{
        return repo.getSession().asLiveData()
    }
    suspend fun postLogin(reqLogin: RequestLogin): LiveData<ResponseLogin> {
        return repo.postLogin(reqLogin).asFlow().asLiveData()
    }
}