package com.charlye934.chat.login.fragment

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.charlye934.chat.R
import com.charlye934.chat.databinding.FragmentLoginBinding
import com.charlye934.chat.login.LoginActivity
import com.charlye934.chat.login.listener.LoginListener
import com.charlye934.chat.utils.FirebaseInstance
import com.charlye934.chat.utils.isValidEmail
import com.charlye934.chat.utils.isValidPassword
import com.google.android.gms.auth.api.identity.*
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var mCallback: LoginListener

    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest

    private val mGoogleClient by lazy { getGoogleApiClient()}
    private val mAuth: FirebaseAuth = FirebaseInstance.mAuth
    private val RC_CODE_SIIGN_IN = 100
    private val REQ_VAL_TAP = 200

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setDataOneTapeGoogle()
        loginOneTapeGoogle()
        setOnClick()
    }

    private fun setDataOneTapeGoogle(){
        oneTapClient = Identity.getSignInClient(requireContext())
        signInRequest = BeginSignInRequest.builder()
            .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                .setSupported(true)
                .build()
            )
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId("PRUEBA")
                    .setFilterByAuthorizedAccounts(true)
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }

    private fun loginOneTapeGoogle(){
        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener{
                try {
                    startIntentSenderForResult(
                        it.pendingIntent.intentSender,
                        REQ_VAL_TAP,
                        null, 0,0,0,null
                    )
                }catch (e: IntentSender.SendIntentException){
                    Log.e(TAG, "Couldn't start One Tap UI: ${e.localizedMessage}")
                }
            }
            .addOnFailureListener{ e ->
                Log.d(TAG, e.localizedMessage)
            }
    }

    private fun setOnClick() {
        with(binding){
            buttonCreateAccount.setOnClickListener {
                mCallback.goToSignUp()
            }
            textViewForgotPassword.setOnClickListener {
                mCallback.goToForgotPassword()
            }
            btnLoginGoogle.setOnClickListener {
                val sigIntent = mGoogleClient.signInIntent
                startActivityForResult(sigIntent, RC_CODE_SIIGN_IN)
            }
            btnLogin.setOnClickListener {
                signInLogin()
            }
        }
    }

    private fun signInLogin(){
        val email = binding.editTextEmail.text.toString().trim()
        val password = binding.editTextPassword.text.toString().trim()

        if(isValidEmail(email, binding.inputLayoutEmail) && isValidPassword(password, binding.inputLayoutPassword)){
            loginByEmail(email, password)
        }else{
            Toast.makeText(context, "Favor de llenar los campos correctamente", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loginByEmail(email:String, password:String){
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    if(mAuth.currentUser!!.isEmailVerified){
                       mCallback.goToHome()
                    }else{
                        Toast.makeText(context, "User must confirm email first", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "An unexpected error ocurred, please try again", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun getGoogleApiClient(): GoogleSignInClient {
        val gson = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(requireContext(), gson)
    }

    private fun loginByGoogleAndFirease(googleAccount: String){
        val credential = GoogleAuthProvider.getCredential(googleAccount, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()){
               if(it.isSuccessful){
                   if(GoogleSignIn.getLastSignedInAccount(requireContext()) != null)
                       mGoogleClient.signOut()
                   mCallback.goToHome()
               }else{
                   Log.d("__tag", "fallo")
               }
            }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode){
            REQ_VAL_TAP ->{
                try {
                    val credential = oneTapClient.getSignInCredentialFromIntent(data)
                    val idToken = credential.googleIdToken
                    when {
                        idToken != null -> {
                            loginByGoogleAndFirease(idToken)
                        }
                        else -> {
                            Log.d("__tag", "No ID token or password!")
                        }
                    }
                }catch (e: ApiException){
                        when (e.statusCode) {
                            CommonStatusCodes.CANCELED -> {
                                Log.d("__tag", "One-tap dialog was closed.")
                                // Don't re-prompt the user.
                                //showOneTapUI = false
                            }
                            CommonStatusCodes.NETWORK_ERROR -> {
                                Log.d("__tag", "One-tap encountered a network error.")
                                // Try again or just ignore.
                            }
                            else -> {
                                Log.d("__tag", "Couldn't get credential from result." +
                                        " (${e.localizedMessage})")
                            }
                        }
                }
            }

            RC_CODE_SIIGN_IN ->{
                val credential = GoogleSignIn.getSignedInAccountFromIntent(data)

                try{
                    val account = credential.getResult(ApiException::class.java)
                    loginByGoogleAndFirease(account!!.idToken!!)
                }catch (e: ApiException){
                    Log.d("__TAG", "Google sign in failed ${e.message}")
                }
            }
            }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mCallback = activity as LoginActivity
    }

    companion object {
        val TAG = LoginFragment::class.java.canonicalName
        fun newInstance() = LoginFragment()
    }
}