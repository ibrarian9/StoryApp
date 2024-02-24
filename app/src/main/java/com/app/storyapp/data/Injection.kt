package com.app.storyapp.data

import android.content.Context

object Injection {
    fun provideRepo(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        return UserRepository.getInstance(pref)
    }
}