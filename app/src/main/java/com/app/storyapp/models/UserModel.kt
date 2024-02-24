package com.app.storyapp.models

data class UserModel (
    val email: String,
    val token: String,
    val isLogin: Boolean = false
)