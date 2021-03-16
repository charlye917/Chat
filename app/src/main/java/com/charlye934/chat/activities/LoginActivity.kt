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
import com.example.chat.R
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlin.math.log

class LoginActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {

    private lateinit var binding: ActivityLoginBinding
    private val mGoogleClient by lazy { getGoogleApiClient()}
    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val RC_CODE_SIIGN_IN = 100

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
                val sigIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleClient)
                startActivityForResult(sigIntent, RC_CODE_SIIGN_IN)
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

    private fun getGoogleApiClient(): GoogleApiClient? {
        val gson = GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        return GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gson)
                .build()
    }

    private fun loginByGoogleAndFirease(googleAccount: GoogleSignInAccount){
        val credential = GoogleAuthProvider.getCredential(googleAccount.idToken, null)
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this){
                    if(mGoogleClient!!.isConnected){
                        Auth.GoogleSignInApi.signOut(mGoogleClient)
                    }
                    goToActivity<MainActivity>{
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.d("__TAG", requestCode.toString())
        if(requestCode == RC_CODE_SIIGN_IN){
            val result = GoogleSignIn.getSignedInAccountFromIntent(data)
            try{
                val account = result.getResult(ApiException::class.java)
                loginByGoogleAndFirease(account!!)
            }catch (e: ApiException){
                Log.d("__TAG", "Google sign in failed", e)
            }
        }
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Toast.makeText(applicationContext, "Connection Failed",Toast.LENGTH_SHORT).show()
    }
}