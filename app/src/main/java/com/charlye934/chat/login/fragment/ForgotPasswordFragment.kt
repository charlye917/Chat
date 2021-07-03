package com.charlye934.chat.login.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.charlye934.chat.R
import com.charlye934.chat.databinding.FragmentForgotPasswordBinding
import com.charlye934.chat.login.LoginActivity
import com.charlye934.chat.login.listener.LoginListener
import com.charlye934.chat.utils.FirebaseInstance
import com.charlye934.chat.utils.isValidEmail

class ForgotPasswordFragment : Fragment() {

    private val mAuth = FirebaseInstance.mAuth
    private lateinit var mCallback: LoginListener
    private lateinit var binding: FragmentForgotPasswordBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setOnclick()
    }

    private fun setOnclick() {
        binding.apply {
            buttonForgot.setOnClickListener { validateEmail() }
            buttonGoLoginForgot.setOnClickListener { mCallback.goToLogin()}
        }
    }

    private fun validateEmail() {
        val email = binding.editTextEmailForgot.text.toString()
        if(isValidEmail(email)){
            mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener {
                    Toast.makeText(context, "Email has been sent to reset to password", Toast.LENGTH_SHORT).show()
                    mCallback.goToLogin()
                }
        }else{
            binding.editTextEmailForgot.error = "Email is not valid"
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mCallback = activity as LoginActivity
    }

    companion object {
        val TAG = ForgotPasswordFragment::class.java.canonicalName
        fun newInstances() = ForgotPasswordFragment()
    }
}