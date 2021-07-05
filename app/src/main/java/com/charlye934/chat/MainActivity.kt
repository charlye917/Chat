package com.charlye934.chat

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.charlye934.chat.home.HomeActivity
import com.charlye934.chat.utils.goToActivity
import com.charlye934.chat.login.LoginActivity
import com.charlye934.chat.utils.BiometricUtil
import com.charlye934.chat.utils.FirebaseInstance
import java.util.concurrent.Executor

class MainActivity : AppCompatActivity(), BiometricListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(R.style.SplashTheme)

        if(FirebaseInstance.mAuth.currentUser == null){
            goToLogin()
        }else{
            BiometricUtil.showBiometricPrompt(activity = this,listener = this)
        }

        //finish()
    }

    override fun goToLogin() {
        goToActivity<LoginActivity>{
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
    }

    override fun goToHome() {
        goToActivity<HomeActivity>{
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
    }
}