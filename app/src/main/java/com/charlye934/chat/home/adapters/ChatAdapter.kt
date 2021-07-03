package com.charlye934.chat.home.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.charlye934.chat.home.models.Message
import com.charlye934.chat.R
import com.charlye934.chat.databinding.FragmentChatItemLeftBinding
import com.charlye934.chat.databinding.FragmentChatItemRightBinding
import java.text.SimpleDateFormat

class ChatAdapter(val items: ArrayList<Message>, val userId:String, val context:Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val GLOBAL_MESSAGE = 1
    private val MY_MESSAGE = 2

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int) = if(items[position].authorId == userId) MY_MESSAGE else GLOBAL_MESSAGE

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            MY_MESSAGE -> ViewHolderR(FragmentChatItemRightBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            else -> ViewHolderL(FragmentChatItemLeftBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder.itemViewType){
            MY_MESSAGE -> (holder as ViewHolderR).bind(items[position])
            GLOBAL_MESSAGE -> (holder as ViewHolderL).bind(items[position])
        }
    }

    inner class ViewHolderR(private val bindign: FragmentChatItemRightBinding): RecyclerView.ViewHolder(bindign.root){
        fun bind(message: Message) = with(bindign){
            textViewMessageRight.text = message.message
            textViewTimeRight.text = SimpleDateFormat("hh:mm").format(message.sentAt)
            if(message.profileImageURL.isEmpty())
                Glide.with(context)
                    .load(R.drawable.ic_person)
                    .circleCrop()
                    .apply(RequestOptions.overrideOf(100,100))
                    .into(imageViewProfileRight)
            else
                Glide.with(context)
                    .load(message.profileImageURL)
                    .circleCrop()
                    .apply(RequestOptions.overrideOf(100,100))
                    .into(imageViewProfileRight)
        }
    }

    inner class ViewHolderL(private val bindign: FragmentChatItemLeftBinding): RecyclerView.ViewHolder(bindign.root){
        fun bind(message: Message) = with(bindign){

            textViewMessageLeft.text = message.message
            textViewTimeLeft.text = SimpleDateFormat("hh:mm").format(message.sentAt)
            if(message.profileImageURL.isEmpty())
                Glide.with(context)
                    .load(R.drawable.ic_account)
                    .circleCrop()
                    .apply(RequestOptions.overrideOf(100,100))
                    .into(imageViewProfileLeft)
            else
                Glide.with(context)
                    .load(message.profileImageURL)
                    .circleCrop()
                    .apply(RequestOptions.overrideOf(100,100))
                    .into(imageViewProfileLeft)
        }
    }
}