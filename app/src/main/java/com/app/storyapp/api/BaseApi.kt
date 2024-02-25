package com.app.storyapp.api

import com.app.storyapp.models.ListStoryItem
import com.app.storyapp.models.RequestLogin
import com.app.storyapp.models.RequestRegister
import com.app.storyapp.models.ResponseDetailStory
import com.app.storyapp.models.ResponseListStory
import com.app.storyapp.models.ResponseLogin
import com.app.storyapp.models.ResponseRegister
import com.app.storyapp.models.ResponseUploadStory
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

class BaseApi {

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://story-api.dicoding.dev/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getService(): urlData = getRetrofit().create(urlData::class.java)
}

interface urlData {

    @POST("register")
    fun postRegister(@Body reqRegister: RequestRegister): Call<ResponseRegister>

    @POST("login")
    fun postLogin(@Body reqLogin: RequestLogin): Call<ResponseLogin>

    @GET("stories")
    fun getAllStory(
        @Header("Authorization") token: String
    ): Call<ResponseListStory>

    @GET("stories/{id}")
    fun getDetailStory(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Call<ResponseDetailStory>

    @POST("stories")
    @Multipart
    fun postStory(
        @Header("Authorization") token: String,
        @Part photo: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): Call<ResponseUploadStory>
}