package com.charlye934.chat.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.charlye934.chat.MainActivity
import com.example.chat.databinding.ActivityLoginBinding
import com.charlye934.chat.utils.goToActivity
import com.charlye934.chat.utils.isValidEmail
import com.charlye934.chat.utils.isValidPassword
import com.google.firebase.auth.FirebaseAuth
import kotlin.math.log

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setOnClick()
    }

    private fun setOnClick() {
        with(binding){
            buttonCreateAccount.setOnClickListener {
                goToActivity<SignUpActivity>()
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            }
            textViewForgotPassword.setOnClickListener {
                goToActivity<ForgotPasswordActivity>()
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            }
            buttonLoginGoogle.setOnClickListener {

            }
            buttonLogin.setOnClickListener { signInLogin() }
        }
    }

    private fun signInLogin(){
        val email = binding.editTextEmail.text.toString().trim()
        val password = binding.editTextPassword.text.toString().trim()

        if(isValidEmail(email) && isValidPassword(password)){
            loginByEmail(email, password)
        }else{
            Toast.makeText(baseContext, "Favor de llenar los campos correctamente", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loginByEmail(email:String, password:String){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        if(mAuth.currentUser!!.isEmailVerified){
                            goToActivity<MainActivity>{
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                        }else{
                            Toast.makeText(baseContext, "User must confirm email first", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "An unexpected error ocurred, please try again",Toast.LENGTH_SHORT).show()
                    }
                }
    }
}