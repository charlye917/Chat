package com.example.chat.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.chat.databinding.ActivitySignUpBinding
import com.example.chat.utils.goToActivity
import com.example.chat.utils.isValidConfirmPassword
import com.example.chat.utils.isValidEmail
import com.example.chat.utils.isValidPassword
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {

    private val mAunt: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setOnClickListener()
    }

    private fun setOnClickListener(){
        with(binding){
            buttonSignUp.setOnClickListener { checkData() }
            buttonGoLogin.setOnClickListener {
                goToActivity<LoginActivity> {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            }
        }
    }

    private fun checkData(){
        val email = binding.editTextEmail.text.toString().trim()
        val password = binding.editTextPassword.text.toString().trim()

        if(validateEmail(email) && validatePassword(password)){
            createAccount(email, password)
        }else{
            Toast.makeText(this, "Favor de llenar los correctamente", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createAccount(email: String, password: String){
        mAunt.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    mAunt.currentUser!!.sendEmailVerification().addOnCompleteListener(this) {
                        Toast.makeText(this, "An email has been sent to you. Please, confirm before sign in.", Toast.LENGTH_SHORT).show()
                        goToActivity<LoginActivity> {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    }
                } else {
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun validateEmail(email: String): Boolean{
        return if(email.isNotEmpty()) {
            if(isValidEmail(email)){
                binding.textInputLayoutEmail.error = null
                true
            }else{
                binding.textInputLayoutEmail.error = "FAVOR DE INGRESAR UN EMAIL VALIDO"
                false
            }
        }
        else {
            binding.textInputLayoutEmail.error = "FAVOR DE INGRESAR UN EMAIL VALIDO"
            false
        }
    }

    private fun validatePassword(password: String,): Boolean{
        val confirmPass = binding.editTextConfirmPassword.text.toString().trim()
            return if(password.isNotEmpty()){
                binding.textInputLayoutPassword.error = null
                if(isValidPassword(password)){
                    binding.textInputLayoutPassword.error = null
                    if(isValidConfirmPassword(password, confirmPass)){
                        binding.textInputLayoutConfirmPassword.error = null
                        true
                    }else{
                        binding.textInputLayoutConfirmPassword.error = "LAS CONTRASEÑAS NO COINCIDEN"
                        false
                    }
                }else{
                    binding.textInputLayoutPassword.error = "Favor de ingesar un password valido"
                    false
                }
            }else{
                binding.textInputLayoutPassword.error = "Favor de ingesar un password valido"
                false
            }
        }
}