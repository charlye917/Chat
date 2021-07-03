package com.charlye934.chat.home.fragment

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.charlye934.chat.home.adapters.ChatAdapter
import com.charlye934.chat.databinding.FragmentChatBinding
import com.charlye934.chat.home.models.Message
import com.charlye934.chat.home.models.TotalMessagesEvent
import com.charlye934.chat.utils.RxBus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import java.util.*
import java.util.EventListener
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding

    private lateinit var adapterChat: ChatAdapter
    private val messageList: ArrayList<Message> = ArrayList()

    private val mAut: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var currentUser: FirebaseUser

    private val store: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var chatDBRef: CollectionReference

    private var chatSubscription: ListenerRegistration? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentChatBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.layout

        setUpChatDB()
        setUpCurrentUser()
        setUpRecycerView()
        setUpChatBtn()

        subscribeToChatMessages()
    }

    private fun setUpChatDB(){
        chatDBRef = store.collection("chat")
    }

    private fun setUpCurrentUser(){
        currentUser = mAut.currentUser!!
    }

    private fun setUpRecycerView(){
        val layoutManagerChat = LinearLayoutManager(context)
        adapterChat = ChatAdapter(messageList, currentUser.uid, requireContext())

        binding.recyclerVew.apply {
            setHasFixedSize(true)
            layoutManager = layoutManagerChat
            itemAnimator = DefaultItemAnimator()
            adapter = adapterChat
        }

    }

    private fun setUpChatBtn(){
        binding.buttonSend.setOnClickListener {
            val messageText = binding.etMessage.text.toString()
            if(messageText.isNotEmpty()){
                val photo = currentUser.photoUrl?.let { currentUser.photoUrl.toString() } ?: run {""}
                val message = Message(currentUser.uid, messageText, photo, Date())
                saveMessage(message)
                binding.etMessage.setText("")
            }
        }
    }

    private fun saveMessage(message: Message){
        val newMessage = HashMap<String, Any>()
        newMessage["authorId"] = message.authorId
        newMessage["message"] = message.message
        newMessage["profileImageURL"] = message.profileImageURL
        newMessage["sentAt"] = message.sentAt

        chatDBRef.add(newMessage)
            .addOnCompleteListener {
                Toast.makeText(context,"Message added!",Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context,"Message error, try again!",Toast.LENGTH_SHORT).show()
            }
    }

    private fun subscribeToChatMessages(){
        chatSubscription = chatDBRef
            .orderBy("sentAt", Query.Direction.DESCENDING)
            .limit(100)
            .addSnapshotListener(object : EventListener, com.google.firebase.firestore.EventListener<QuerySnapshot>{
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    error?.let {
                        Toast.makeText(context,"Exception",Toast.LENGTH_SHORT).show()
                        return
                    }

                    value?.let {
                        messageList.clear()
                        val message = it.toObjects(Message::class.java)
                        messageList.addAll(message.asReversed())
                        adapterChat.notifyDataSetChanged()
                        binding.recyclerVew.smoothScrollToPosition(messageList.size)
                        RxBus.publish(TotalMessagesEvent(messageList.size))
                    }
                }
            })
    }

    override fun onDestroyView() {
        chatSubscription?.remove()
        super.onDestroyView()
    }
}