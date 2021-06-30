package com.charlye934.chat.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.charlye934.chat.databinding.ActivityLoginBinding
import com.charlye934.chat.R
import com.charlye934.chat.activities.ForgotPasswordActivity
import com.charlye934.chat.activities.HomeActivity
import com.charlye934.chat.activities.SignUpActivity
import com.charlye934.chat.login.fragment.LoginFragment
import com.charlye934.chat.login.listener.LoginListener
import com.charlye934.chat.utils.goToActivity

class LoginActivity : AppCompatActivity(), LoginListener{

    private lateinit var binding: ActivityLoginBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        changeFragment(LoginFragment.newInstance(), LoginFragment.TAG!!)
    }

    override fun goToForgotPassword() {
        goToActivity<ForgotPasswordActivity>()
    }

    override fun goToSignUp() {
        goToActivity<SignUpActivity>()
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
}