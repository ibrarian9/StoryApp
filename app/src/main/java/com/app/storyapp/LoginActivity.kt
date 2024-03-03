package com.app.storyapp

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.app.storyapp.databinding.ActivityLoginBinding
import com.app.storyapp.models.RequestLogin
import com.app.storyapp.viewModels.LoginViewModels
import com.app.storyapp.viewModels.ViewModelsFactory
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var bind: ActivityLoginBinding
    private val loginViewModel by viewModels<LoginViewModels> {
        ViewModelsFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(bind.root)

        getLoginData()
        animation()
    }

    private fun getLoginData() {
        bind.loginButton.setOnClickListener {
            val dataEmail = bind.edEmail.text.toString()
            val dataPass = bind.edPassword.text.toString()

            when {
                dataEmail.isEmpty() -> pesanError("Email Masih Kosong...")
                dataPass.isEmpty() -> pesanError("Password Masih Kosong...")
                else -> {
                    hideOrshowLoading(true)
                    disableBtn()
                    getResponse(dataEmail, dataPass)
                }
            }
        }
    }

    private fun disableBtn() {
        bind.loginButton.isEnabled = !bind.loading.isVisible
        bind.loginButton.isClickable = !bind.loading.isVisible
    }

    private fun getResponse(dataEmail: String, dataPass: String) {
        val inputLogin = RequestLogin(dataEmail, dataPass)
        lifecycleScope.launch {
            try {
                loginViewModel.postLogin(inputLogin)
                loginViewModel.getSession().observe(this@LoginActivity){
                    if (it.isLogin){
                        hideOrshowLoading(false)
                        notifSuccess()
                    } else {
                        hideOrshowLoading(false)
                        disableBtn()
                        notifFailed()
                    }
                }
            } catch (e: Exception) {
                hideOrshowLoading(false)
                disableBtn()
                notifFailed()
            }
        }
    }

    private fun hideOrshowLoading(i: Boolean) {
        bind.loading.isVisible = i
    }

    private fun notifFailed() {
        AlertDialog.Builder(this).apply {
            setTitle("Failed")
            setMessage("Anda Gagal Login....")
            setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            create()
            show()
        }
    }

    private fun notifSuccess() {
        AlertDialog.Builder(this).apply {
            setTitle("Yeah")
            setMessage("Anda Berhasil Login....")
            setPositiveButton("lanjut") { _, _ ->
                val i = Intent(context, ListStoryActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(i)
                finish()
            }
            create()
            show()
        }
    }

    private fun pesanError(s: String) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
    }

    private fun animation() {
        ObjectAnimator.ofFloat(bind.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 1000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(bind.titleTextView, View.ALPHA, 1f).setDuration(200)
        val message =
            ObjectAnimator.ofFloat(bind.messageTextView, View.ALPHA, 1f).setDuration(200)
        val emailTextView =
            ObjectAnimator.ofFloat(bind.emailTextView, View.ALPHA, 1f).setDuration(200)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(bind.emailEditTextLayout, View.ALPHA, 1f).setDuration(200)
        val passwordTextView =
            ObjectAnimator.ofFloat(bind.passwordTextView, View.ALPHA, 1f).setDuration(200)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(bind.passwordEditTextLayout, View.ALPHA, 1f).setDuration(200)
        val login = ObjectAnimator.ofFloat(bind.loginButton, View.ALPHA, 1f).setDuration(200)

        AnimatorSet().apply {
            playSequentially(
                title,
                message,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                login
            )
            startDelay = 200
        }.start()
    }
}