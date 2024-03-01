package com.app.storyapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.app.storyapp.api.urlData
import com.app.storyapp.models.ListStoryItem
import com.app.storyapp.models.ResponseDetailStory
import com.app.storyapp.models.ResponseListStory
import com.app.storyapp.models.ResponseUploadStory
import com.app.storyapp.models.Story
import com.app.storyapp.models.UserModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoryRepository private constructor (
    private val userPreference: UserPreference, private val apiService: urlData
) {

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    suspend fun getDetailStory(id: String): Flow<Story> = callbackFlow {
        val call = apiService.getDetailStory(id)
        call.enqueue(object : Callback<ResponseDetailStory> {
            override fun onResponse(
                call: Call<ResponseDetailStory>,
                response: Response<ResponseDetailStory>
            ) {
                if (response.isSuccessful) {
                    val story = response.body()?.story
                    if (story != null){
                        trySend(story)
                    } else {
                        close(Exception("Data Tidak Ada..."))
                    }
                } else {
                    close(Exception("Unsuccessful response: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<ResponseDetailStory>, t: Throwable) {
                close(t)
            }
        })

        awaitClose {
            call.cancel()
        }
    }

    fun getStory(): LiveData<List<ListStoryItem>> {
        val data = MutableLiveData<List<ListStoryItem>>()
        val call = apiService.getStory()

        call.enqueue(object : Callback<ResponseListStory> {
            override fun onResponse(
                call: Call<ResponseListStory>,
                response: Response<ResponseListStory>
            ) {
                if (response.isSuccessful){
                    data.value = response.body()!!.listStory ?: emptyList()
                }
            }

            override fun onFailure(call: Call<ResponseListStory>, t: Throwable) {
                t.message
            }

        })
        return data
    }

    fun getAllStory(): LiveData<PagingData<ListStoryItem>> {

        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false,
                prefetchDistance = 4 * PAGE_SIZE,
                initialLoadSize = 3 * PAGE_SIZE
            ),
            pagingSourceFactory = {
                PagingStory(apiService)
            }, initialKey = 1
        ).liveData
    }

    suspend fun postStory(poto: MultipartBody.Part, desc: RequestBody): LiveData<ResponseUploadStory> {
        val data = MutableLiveData<ResponseUploadStory>()

        try {
            val response = apiService.postStory(poto, desc)
            if (response.isSuccessful){
                data.postValue(response.body())
            }
        } catch (e: Exception){
            e.message
        }
        return data
    }

    companion object {
        private const val PAGE_SIZE = 10

        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
          userPref: UserPreference, apiService: urlData
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(userPref, apiService)
            }.also { instance = it }
    }
}