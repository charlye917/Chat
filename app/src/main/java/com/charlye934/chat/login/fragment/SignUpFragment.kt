package com.charlye934.chat.login.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.charlye934.chat.R
import com.charlye934.chat.databinding.FragmentSignUpBinding
import com.charlye934.chat.login.LoginActivity
import com.charlye934.chat.login.listener.LoginListener
import com.charlye934.chat.utils.FirebaseInstance
import com.charlye934.chat.utils.isValidConfirmPassword
import com.charlye934.chat.utils.isValidEmail
import com.charlye934.chat.utils.isValidPassword

class SignUpFragment : Fragment() {

    private val mAunt = FirebaseInstance.mAuth
    private lateinit var mCallback: LoginListener
    private lateinit var binding: FragmentSignUpBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setOnClickListener()
    }

    private fun setOnClickListener() {
        with(binding){
            buttonSignUp.setOnClickListener { checkData() }
            buttonGoLogin.setOnClickListener { mCallback.goToLogin()}
        }
    }

    private fun checkData() {
        val email = binding.editTextEmail.text.toString().trim()
        val password = binding.editTextPassword.text.toString().trim()

        if(isValidEmail(email, binding.textInputLayoutEmail) && validatePassword(password)){
            createAccount(email, password)
        }else{
            Toast.makeText(context, "Favor de llenar los correctamente", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createAccount(email:String, password: String){
        mAunt.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if(it.isSuccessful){
                    mAunt.currentUser!!.sendEmailVerification().addOnCompleteListener {
                        Toast.makeText(context, "An email has been sent to you. Please, confirm before sign in.", Toast.LENGTH_SHORT).show()
                        mCallback.goToLogin()
                    }
                }else{
                    Toast.makeText(context, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun validatePassword(password: String): Boolean{
        val confirmPass = binding.editTextConfirmPassword.text.toString().trim()
        return if(isValidPassword(password)){
            if(isValidConfirmPassword(password, confirmPass)){
                binding.textInputLayoutConfirmPassword.error = null
                true
            }else{
                binding.textInputLayoutConfirmPassword.error = "LAS CONTRASEÑAS NO COINCIDEN"
                false
            }
        }else{
            false
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mCallback = activity as LoginActivity
    }

    companion object{
        val TAG = SignUpFragment::class.java.canonicalName
        fun newInstance() = SignUpFragment()
    }

}