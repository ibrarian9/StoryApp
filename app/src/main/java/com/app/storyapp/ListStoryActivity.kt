package com.app.storyapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.storyapp.adapter.ListStoryAdapter
import com.app.storyapp.api.BaseApi
import com.app.storyapp.databinding.ActivityListStoryBinding
import com.app.storyapp.models.ListStoryItem
import com.app.storyapp.models.PlaceModel
import com.app.storyapp.models.ResponseListStory
import com.app.storyapp.models.UserModel
import com.app.storyapp.viewModels.ListStoryModels
import com.app.storyapp.viewModels.ViewModelsFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListStoryActivity : AppCompatActivity() {

    private val listStoryViewModel by viewModels<ListStoryModels> {
        ViewModelsFactory.getInstance(this)
    }
    private lateinit var bind: ActivityListStoryBinding
    private val listStoryAdapter by lazy { ListStoryAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityListStoryBinding.inflate(layoutInflater)
        setContentView(bind.root)

        getToken()

        bind.btnMap.setOnClickListener {
            startActivity(Intent(this@ListStoryActivity, MapsActivity::class.java))
        }
        bind.btnAddStory.setOnClickListener {
            startActivity(Intent(this@ListStoryActivity, AddStoryActivity::class.java))
        }
        listStoryViewModel.getSession().observe(this) {
            if (!it.isLogin){
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }
        bind.btnLogout.setOnClickListener {
            listStoryViewModel.logout()
            val i = Intent(this@ListStoryActivity, WelcomeActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(i)
            finish()
        }
    }

    private fun getDataStory(it: UserModel) {
        val token = "Bearer ${it.token}"
        val callApi = BaseApi().getService().getAllStory(token)

        callApi.enqueue(object: Callback<ResponseListStory> {
            override fun onResponse(
                call: Call<ResponseListStory>,
                response: Response<ResponseListStory>
            ) {
                if (response.isSuccessful){
                    val data: List<ListStoryItem> = response.body()?.listStory ?: emptyList()
                    listStoryAdapter.differ.submitList(data)
                    setAdapter()
                }
            }

            override fun onFailure(call: Call<ResponseListStory>, t: Throwable) {
                println("Error = ${t.message}")
            }

        })
    }

    private fun setAdapter() {

        bind.rv.apply {
            layoutManager = LinearLayoutManager(this@ListStoryActivity)
            adapter = listStoryAdapter
            setHasFixedSize(true)
        }
    }

    private fun getToken() {
        listStoryViewModel.getSession().observe(this){
            getDataStory(it)
        }
    }


}