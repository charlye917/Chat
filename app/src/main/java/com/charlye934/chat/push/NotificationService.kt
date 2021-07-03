package com.charlye934.chat.push

import android.util.Log
import android.widget.Toast
import com.charlye934.chat.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService

class NotificationService: FirebaseMessagingService() {
    override fun onNewToken(p0: String) {
        Log.d("__tag","new notificaiton")
    }
}