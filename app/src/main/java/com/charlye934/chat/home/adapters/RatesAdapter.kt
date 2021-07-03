package com.charlye934.chat.home.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.charlye934.chat.R
import com.charlye934.chat.databinding.FragmentRatesItemBinding
import com.charlye934.chat.home.models.Rates
import java.text.SimpleDateFormat

class RatesAdapter(private val items: List<Rates>, private val  context:Context) : RecyclerView.Adapter<RatesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(FragmentRatesItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(private val binding: FragmentRatesItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(rate: Rates) = with(binding){
            binding.textViewRate.text = rate.text
            binding.textViewStar.text = rate.rate.toString()
            binding.textViewCalendar.text = SimpleDateFormat("dd MM, yyyy").format(rate.createdAt)
            if(rate.profileImgURL.isEmpty()){
                Glide.with(context)
                    .load(R.drawable.ic_account)
                    .circleCrop()
                    .into(imageViewProfile)
            }else{
                Glide.with(context)
                    .load(rate.profileImgURL)
                    .circleCrop()
                    .into(imageViewProfile)
            }
        }
    }
}