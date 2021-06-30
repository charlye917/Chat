package com.charlye934.chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.charlye934.chat.activities.HomeActivity
import com.charlye934.chat.utils.goToActivity
import com.charlye934.chat.login.LoginActivity
import com.charlye934.chat.utils.FirebaseInstance

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(R.style.SplashTheme)

        if(FirebaseInstance.mAuth.currentUser == null){
            goToActivity<LoginActivity>{
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        }else{
            goToActivity<HomeActivity>{
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            }
        }

        finish()
    }
}