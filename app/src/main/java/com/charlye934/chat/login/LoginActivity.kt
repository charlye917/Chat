package com.charlye934.chat.login

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.charlye934.chat.databinding.ActivityLoginBinding
import com.charlye934.chat.R
import com.charlye934.chat.home.HomeActivity
import com.charlye934.chat.login.fragment.ForgotPasswordFragment
import com.charlye934.chat.login.fragment.LoginFragment
import com.charlye934.chat.login.fragment.SignUpFragment
import com.charlye934.chat.login.listener.LoginListener
import com.charlye934.chat.utils.goToActivity

class LoginActivity : AppCompatActivity(), LoginListener{

    private lateinit var binding: ActivityLoginBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        goToLogin()
    }

    override fun goToLogin() {
        changeFragment(LoginFragment.newInstance(), LoginFragment.TAG!!)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    override fun goToForgotPassword() {
        changeFragment(ForgotPasswordFragment.newInstances(), ForgotPasswordFragment.TAG!!)
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
    }

    override fun goToSignUp() {
        changeFragment(SignUpFragment.newInstance(), SignUpFragment.TAG!!)
        overridePendingTransition(android.R.anim.fade_out, android.R.anim.fade_in)
    }

    override fun goToHome() {
        goToActivity<HomeActivity>{
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
    }

    private fun changeFragment(fragment: Fragment, tag: String){
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frameActivityLogin, fragment)
            .addToBackStack(tag)
            .commit()

        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
    }

    @SuppressLint("ResourceType")
    override fun onBackPressed() {

        val fragment = supportFragmentManager.findFragmentById(R.id.frameActivityLogin)
        when(fragment){
            is LoginFragment ->{
                finish()
            }
            else -> { super.onBackPressed() }
        }

    }
}