package com.app.storyapp.data

import android.content.Context
import com.app.storyapp.api.BaseApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepo(context: Context): StoryRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = BaseApi().getApiService(user.token)
        return StoryRepository.getInstance(pref, apiService)
    }
}