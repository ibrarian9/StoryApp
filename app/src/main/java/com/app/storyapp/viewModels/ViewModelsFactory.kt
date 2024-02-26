package com.app.storyapp.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.storyapp.AddStoryActivity
import com.app.storyapp.data.Injection
import com.app.storyapp.data.UserRepository

class ViewModelsFactory(private val repo: UserRepository): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModels::class.java) -> {
                LoginViewModels(repo) as T
            }
            modelClass.isAssignableFrom(ListStoryModels::class.java) -> {
                ListStoryModels(repo) as T
            }
            modelClass.isAssignableFrom(DetailStoryModels::class.java) -> {
                DetailStoryModels(repo) as T
            }
            modelClass.isAssignableFrom(AddStoryModels::class.java) -> {
                AddStoryModels(repo) as T
            }
            modelClass.isAssignableFrom(MapStoryModels::class.java) -> {
                MapStoryModels(repo) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel Class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelsFactory? = null
        @JvmStatic
        fun getInstance(context: Context): ViewModelsFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelsFactory::class.java) {
                    INSTANCE = ViewModelsFactory(Injection.provideRepo(context))
                }
            }
            return INSTANCE as ViewModelsFactory
        }
    }
}