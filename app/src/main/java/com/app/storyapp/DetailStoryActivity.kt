package com.app.storyapp

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.app.storyapp.api.BaseApi
import com.app.storyapp.databinding.ActivityDetailStoryBinding
import com.app.storyapp.models.ResponseDetailStory
import com.app.storyapp.models.Story
import com.app.storyapp.models.UserModel
import com.app.storyapp.viewModels.DetailStoryModels
import com.app.storyapp.viewModels.ViewModelsFactory
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailStoryActivity : AppCompatActivity() {

    private val detailStoryModels by viewModels<DetailStoryModels> {
        ViewModelsFactory.getInstance(this)
    }

    private lateinit var bind: ActivityDetailStoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(bind.root)

        getDataFromIntent()
    }

    private fun getDataFromIntent() {
        val dataId = intent.getStringExtra("id")

        detailStoryModels.getSession().observe(this) {
            handleData(it, dataId!!)
        }
    }

    private fun handleData(it: UserModel, dataId: String) {
        val token = "Bearer ${it.token}"
        val callApi = BaseApi().getService().getDetailStory(token, dataId)

        callApi.enqueue(object : Callback<ResponseDetailStory> {
            override fun onResponse(
                call: Call<ResponseDetailStory>,
                response: Response<ResponseDetailStory>
            ) {
                if (response.isSuccessful){
                    val data = response.body()?.story
                    fetchDataToBind(data)
                }
            }

            override fun onFailure(call: Call<ResponseDetailStory>, t: Throwable) {
                println("Pesan error = ${t.message}")
            }
        })
    }

    private fun fetchDataToBind(data: Story?) {
        if (data != null){
            bind.storyJudul.text = data.name
            Picasso.get().load(data.photoUrl).fit().into(bind.storyPoto)
            bind.storyDesc.text = data.description
        }
    }
}