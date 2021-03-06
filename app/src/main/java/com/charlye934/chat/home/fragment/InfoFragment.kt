package com.charlye934.chat.home.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.charlye934.chat.R
import com.charlye934.chat.databinding.FragmentInfoBinding
import com.charlye934.chat.home.models.TotalMessagesEvent
import com.charlye934.chat.utils.RxBus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import io.reactivex.rxjava3.disposables.Disposable

class InfoFragment : Fragment() {

    private lateinit var binding: FragmentInfoBinding

    private val mAuth = FirebaseAuth.getInstance()
    private lateinit var currentUser: FirebaseUser

    private val store = FirebaseFirestore.getInstance()
    private lateinit var chatDBRef: CollectionReference

    private var chatSubscription: ListenerRegistration? = null
    private lateinit var inofBusListener: Disposable

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInfoBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpChatDB()
        setUpCurrentUser()
        setUpCurrentUserInforUI()
        sbuscribeToTotalMessagesEventBusReactiveStyle()
        //subscribeToTotalMessageFirebaseStyle()
    }

    private fun setUpChatDB(){
        chatDBRef = store.collection("chat")
    }

    private fun setUpCurrentUser(){
        currentUser = mAuth.currentUser!!
    }

    private fun setUpCurrentUserInforUI(){
        binding.textViewInfoEmail.text = currentUser.email
        binding.textViewInfoName.text = currentUser.displayName?.let { currentUser.displayName } ?: "Name is not avaible"

        currentUser.photoUrl?.let {
            Glide.with(requireContext())
                .load(currentUser.photoUrl)
                .circleCrop()
                .apply(RequestOptions.overrideOf(200,300))
                .into(binding.imageViewInfoAvatar)
        } ?: kotlin.run {
            Glide.with(requireContext())
                .load(R.drawable.ic_person)
                .circleCrop()
                .apply(RequestOptions.overrideOf(100,300))
        }
    }

    private fun sbuscribeToTotalMessagesEventBusReactiveStyle(){
        inofBusListener = RxBus.listen(TotalMessagesEvent::class.java).subscribe {
            binding.textViewInfoLabelChat.text = "Total messages: ${it.total}"
        }
    }

    private fun subscribeToTotalMessageFirebaseStyle(){
         chatSubscription = chatDBRef.addSnapshotListener(object : java.util.EventListener, EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                error?.let {
                    Toast.makeText(context, "Exception", Toast.LENGTH_SHORT).show()
                    return
                }

                value?.let { binding.textViewInfoLabelChat.text = "Total message: ${it.size()}" }
            }

        })
    }

    override fun onDestroyView() {
        chatSubscription?.remove()
        inofBusListener.dispose()
        super.onDestroyView()
    }

}