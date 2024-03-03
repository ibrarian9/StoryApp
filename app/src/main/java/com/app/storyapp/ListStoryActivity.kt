package com.app.storyapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.storyapp.adapter.LoadingStateAdapter
import com.app.storyapp.adapter.PagingStoryAdapter
import com.app.storyapp.databinding.ActivityListStoryBinding
import com.app.storyapp.viewModels.ListStoryModels
import com.app.storyapp.viewModels.ViewModelsFactory
import kotlinx.coroutines.launch

class ListStoryActivity : AppCompatActivity() {

    private val listStoryViewModel by viewModels<ListStoryModels> {
        ViewModelsFactory.getInstance(this)
    }

    private lateinit var bind: ActivityListStoryBinding
    private val pagingStoryAdapter = PagingStoryAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityListStoryBinding.inflate(layoutInflater)
        setContentView(bind.root)

        bind.btnMap.setOnClickListener {
            startActivity(Intent(this@ListStoryActivity, MapsActivity::class.java))
        }
        bind.btnAddStory.setOnClickListener {
            startActivity(Intent(this@ListStoryActivity, AddStoryActivity::class.java))
        }
        listStoryViewModel.getSession().observe(this) {
            val token = it.token
            if (token.isNotEmpty()){
                setAdapter()
            } else {
                startActivity(Intent(this@ListStoryActivity, WelcomeActivity::class.java))
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

    private fun setAdapter() {
        bind.rv.apply {
            layoutManager = LinearLayoutManager(this@ListStoryActivity)
            adapter = pagingStoryAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    pagingStoryAdapter.retry()
                }
            )
        }

        lifecycleScope.launch {
            listStoryViewModel.getAllStory().observe(this@ListStoryActivity) {
                pagingStoryAdapter.submitData(lifecycle, it)
            }
        }
    }
}