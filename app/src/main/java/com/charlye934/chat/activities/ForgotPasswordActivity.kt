package com.charlye934.chat.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.charlye934.chat.utils.goToActivity
import com.charlye934.chat.utils.isValidEmail
import com.example.chat.R
import com.example.chat.databinding.ActivityForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {

    private val mAunt: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setOnclick()
    }

    private fun setOnclick() {
        binding.buttonForgot.setOnClickListener {
            validateEmail()
        }
        binding.buttonGoLoginForgot.setOnClickListener {
            goToActivity<LoginActivity> {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    private fun validateEmail() {
        val email = binding.editTextEmailForgot.text.toString()
        if(isValidEmail(email)){
            mAunt.sendPasswordResetEmail(email).addOnCompleteListener {
                Toast.makeText(this, "Email has been sent to reset to password",Toast.LENGTH_SHORT).show()
                goToActivity<LoginActivity>{
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                }
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
        }else{
            binding.editTextEmailForgot.error = "Email is not valid"
        }
    }
}