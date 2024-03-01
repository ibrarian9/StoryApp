package com.app.storyapp

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.app.storyapp.api.BaseApi
import com.app.storyapp.databinding.ActivityRegisterBinding
import com.app.storyapp.models.RequestRegister
import com.app.storyapp.models.ResponseRegister
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    private lateinit var bind: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(bind.root)

        registerData()
        animation()
    }

    private fun registerData() {
        bind.signupButton.setOnClickListener {
            val dataNama = bind.edName.text.toString()
            val dataEmail = bind.edEmail.text.toString()
            val dataPass = bind.edPass.text.toString()

            when {
                dataNama.isEmpty() -> pesanError("Nama Masih Kosong...")
                dataEmail.isEmpty() -> pesanError("Email Masih Kosong...")
                dataPass.isEmpty() -> pesanError("Password Masih Kosong...")
                else -> {
                    hideOrshowLoading(View.VISIBLE)
                    disableBtn()
                    handleRegister(dataNama, dataEmail, dataPass)
                }
            }
        }
    }

    private fun disableBtn() {
        bind.signupButton.isEnabled = !bind.loading.isVisible
        bind.signupButton.isClickable = !bind.loading.isVisible
    }

    private fun handleRegister(dataNama: String, dataEmail: String, dataPass: String) {
        val dataRegister = RequestRegister(dataNama, dataEmail, dataPass)
        val callApi = BaseApi().getApiService("").postRegister(dataRegister)

        callApi.enqueue(object : Callback<ResponseRegister> {
            override fun onResponse(
                call: Call<ResponseRegister>,
                response: Response<ResponseRegister>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    when {
                        responseBody == null -> {
                            hideOrshowLoading(View.GONE)
                            disableBtn()
                            pesanError("Data Kosong...")
                        }
                        responseBody.error == true -> {
                            hideOrshowLoading(View.GONE)
                            disableBtn()
                            val errorMessage = responseBody.message ?: "404 Error"
                            pesanError("Pesan Error = $errorMessage")
                        }
                        else -> {
                            hideOrshowLoading(View.GONE)
                            pesanError(responseBody.message!!)
                            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                            finish()
                        }
                    }
                } else {
                    hideOrshowLoading(View.GONE)
                    disableBtn()
                    pesanError("Pesan Error = ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ResponseRegister>, t: Throwable) {
                println("Error ${t.message}")
            }
        })
    }

    private fun hideOrshowLoading(i: Int) {
        bind.loading.visibility = i
    }

    private fun pesanError(s: String) {
        Toast.makeText(this@RegisterActivity, s, Toast.LENGTH_SHORT).show()
    }

    private fun animation() {
        ObjectAnimator.ofFloat(bind.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(bind.titleTextView, View.ALPHA, 1f).setDuration(300)
        val nameTextView = ObjectAnimator.ofFloat(bind.nameTextView, View.ALPHA, 1f).setDuration(300)
        val nameEditTextLayout = ObjectAnimator.ofFloat(bind.edNameLayout, View.ALPHA, 1f).setDuration(300)
        val emailTextView = ObjectAnimator.ofFloat(bind.emailTextView, View.ALPHA, 1f).setDuration(300)
        val emailEditTextLayout = ObjectAnimator.ofFloat(bind.edEmailLayout, View.ALPHA, 1f).setDuration(300)
        val passwordTextView = ObjectAnimator.ofFloat(bind.passwordTextView, View.ALPHA, 1f).setDuration(300)
        val passwordEditTextLayout = ObjectAnimator.ofFloat(bind.edPassLayout, View.ALPHA, 1f).setDuration(300)
        val signup = ObjectAnimator.ofFloat(bind.signupButton, View.ALPHA, 1f).setDuration(300)

        AnimatorSet().apply {
            playSequentially(
                title,
                nameTextView,
                nameEditTextLayout,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                signup
            )
            startDelay = 300
        }.start()
    }
}