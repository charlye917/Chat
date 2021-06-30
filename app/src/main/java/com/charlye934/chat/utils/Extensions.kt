package com.charlye934.chat.utils

import android.app.Activity
import android.content.Intent
import android.util.Patterns
import android.widget.EditText
import com.google.android.material.textfield.TextInputLayout
import java.util.regex.Pattern

inline fun <reified T : Activity> Activity.goToActivity(noinline init: Intent.() -> Unit = {}) {
    val intent = Intent(this, T::class.java)
    intent.init()
    startActivity(intent)
}

fun isValidEmail(email: String,inputLayout: TextInputLayout? = null): Boolean {
    val pattern = Patterns.EMAIL_ADDRESS
    return if(pattern.matcher(email).matches()){
        inputLayout?.error = null
        true
    }else{
        inputLayout?.error = "FAVOR DE INGRESAR UN EMAIL VALIDO"
        false
    }
}

fun isValidPassword(password: String, inputLayout: TextInputLayout? = null): Boolean {
    // Necesita Contener -->    1 Num / 1 Minuscula / 1 Mayuscula / 1 Special / Min Caracteres 4
    val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$"
    val pattern = Pattern.compile(passwordPattern)
    return if(pattern.matcher(password).matches()) {
        inputLayout?.error = null
        true
    } else{
        inputLayout?.error = "FAVOR DE INGRESAR UN PASSWORD VALIDO"
        false
    }
}

fun isValidConfirmPassword(password: String, confirmPassword: String): Boolean {
    return password == confirmPassword
}

