package com.app.storyapp.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import com.app.storyapp.data.StoryRepository
import com.app.storyapp.models.ResponseUploadStory
import com.app.storyapp.models.UserModel
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryModels(private val repo: StoryRepository): ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return repo.getSession().asLiveData()
    }

    suspend fun postStory(desc: RequestBody, poto: MultipartBody.Part, lat: RequestBody, lon: RequestBody): LiveData<ResponseUploadStory>{
        return repo.postStory(desc, poto, lat, lon).asFlow().asLiveData()
    }
}