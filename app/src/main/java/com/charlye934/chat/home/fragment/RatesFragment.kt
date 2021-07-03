package com.charlye934.chat.home.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.charlye934.chat.home.adapters.RatesAdapter
import com.charlye934.chat.databinding.FragmentRatesBinding
import com.charlye934.chat.home.dialogues.RateDialog
import com.charlye934.chat.home.models.NewRateEvent
import com.charlye934.chat.home.models.Rates
import com.charlye934.chat.utils.RxBus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import io.reactivex.rxjava3.disposables.Disposable
import java.util.EventListener
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class RatesFragment : Fragment() {

    private lateinit var binding: FragmentRatesBinding
    private val ratesList: ArrayList<Rates> = ArrayList()
    private lateinit var adapterRates: RatesAdapter

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var currentUser: FirebaseUser

    private lateinit var scrollListener: RecyclerView.OnScrollListener

    private val store: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var ratesDBRef: CollectionReference

    private var ratesSubscription: ListenerRegistration? = null
    private lateinit var rateBusListener: Disposable

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRatesBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRatesDB()
        setUpCurrentUser()
        setUpRecyclerView()
        setUpFab()

        subscribeToNewRatigns()
        subscribeToRatings()
    }

    private fun setUpRatesDB(){
        ratesDBRef = store.collection("rates")
    }

    private fun setUpCurrentUser(){
        currentUser = mAuth.currentUser!!
    }

    private fun setUpRecyclerView(){
        val layoutManagerRates = LinearLayoutManager(context)
        adapterRates = RatesAdapter(ratesList, context!!)

        scrollListener = object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if(dy > 0 || dy < 0 && binding.fabRatting.isShown){
                    binding.fabRatting.hide()
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if(newState == RecyclerView.SCROLL_STATE_IDLE){
                    binding.fabRatting.show()
                }
                super.onScrollStateChanged(recyclerView, newState)
            }
        }

        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = layoutManagerRates
            itemAnimator = DefaultItemAnimator()
            adapter = adapterRates
            addOnScrollListener(scrollListener)
        }
    }

    private fun setUpFab(){
        binding.fabRatting.setOnClickListener {
            RateDialog().show(requireFragmentManager(), "")
        }
    }

    private fun hasUserRated(rated: ArrayList<Rates>): Boolean{//PARA SOLO PODER ESCRIBIR UNA RESEÑA
        var result = false
        rated.forEach {
            if(it.userId == currentUser.uid){
                result = true
            }
        }

        return result
    }

    private fun removeFABIfRated(rated: Boolean){
        if(rated){
            binding.fabRatting.hide()
            binding.recyclerView.removeOnScrollListener(scrollListener)
        }
    }

    private fun saveRate(rate: Rates){
        val newRating = HashMap<String,Any>()
        newRating["userId"] = rate.userId
        newRating["text"] = rate.text
        newRating["rate"] = rate.rate
        newRating["createdAt"] = rate.createdAt
        newRating["profileImgURL"] = rate.profileImgURL

        ratesDBRef.add(newRating)
            .addOnCompleteListener {
                Toast.makeText(context, "Rating add", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener{
                Toast.makeText(context, "Rating error, try again", Toast.LENGTH_SHORT).show()
            }
    }

    private fun subscribeToRatings(){
        ratesSubscription = ratesDBRef
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener(object : EventListener, com.google.firebase.firestore.EventListener<QuerySnapshot>{
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    error?.let {
                        Toast.makeText(context, "Exception",Toast.LENGTH_SHORT).show()
                        return
                    }

                    value?.let {
                        ratesList.clear()
                        val rates = it.toObjects(Rates::class.java)
                        ratesList.addAll(rates)
                        removeFABIfRated(hasUserRated(ratesList))
                        adapterRates.notifyDataSetChanged()
                        binding.recyclerView.smoothScrollToPosition(0)//para mandar al usuario al inicio si se agrego un nuevo elemento
                    }
                }
            })
    }

    private fun subscribeToNewRatigns(){
        rateBusListener = RxBus.listen(NewRateEvent::class.java).subscribe {
            saveRate(it.rate)
        }
    }

    override fun onDestroy() {
        binding.recyclerView.removeOnScrollListener(scrollListener)
        ratesSubscription?.remove()
        rateBusListener.dispose()

        super.onDestroy()
    }

    companion object {

    }
}