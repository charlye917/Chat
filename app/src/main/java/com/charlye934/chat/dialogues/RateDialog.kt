package com.charlye934.chat.dialogues

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.charlye934.chat.R
import com.charlye934.chat.databinding.DialogRatesBinding
import com.charlye934.chat.models.NewRateEvent
import com.charlye934.chat.models.Rates
import com.charlye934.chat.utils.RxBus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.util.*

class RateDialog : DialogFragment() {

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var currentUser: FirebaseUser

    private fun setUpCurrentUser(){
        currentUser = mAuth.currentUser!!
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        setUpCurrentUser()
        val binding = DialogRatesBinding.inflate(layoutInflater)

        return AlertDialog.Builder(context)
            .setTitle(R.string.dialog_title)
            .setView(binding.root)
            .setPositiveButton(R.string.dialog_ok){ _, _ ->
                val textRate = binding.editTextRateFeedback.toString()
                if(textRate.isNotEmpty()){
                    val imageUrl = currentUser.photoUrl?.toString() ?: run{""}
                    val rate = Rates(currentUser.uid, textRate, binding.ratingBarFeedback.rating, Date(),imageUrl)
                    RxBus.publish(NewRateEvent(rate))
                }
            }
            .setNegativeButton(getString(R.string.dialog_cancel)){_,_->
                Toast.makeText(context, "Pressed Cancel",Toast.LENGTH_SHORT).show()
            }
            .create()
    }

}