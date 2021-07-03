package com.charlye934.chat.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.viewpager2.widget.ViewPager2
import com.charlye934.chat.home.adapters.PagerAdapter
import com.charlye934.chat.home.fragment.ChatFragment
import com.charlye934.chat.home.fragment.InfoFragment
import com.charlye934.chat.home.fragment.RatesFragment
import com.charlye934.chat.R
import com.charlye934.chat.databinding.ActivityMainBinding
import com.charlye934.chat.login.LoginActivity
import com.charlye934.chat.utils.goToActivity
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var prevBottomSelected: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(findViewById(R.id.toolbar_generic_id))

        setUpViewPager(getPagerAdapter())
        setUpBottomNavigationBar()

    }

    private fun getPagerAdapter(): PagerAdapter {
        var adapter = PagerAdapter(this)
        return adapter.apply {
            addFragment(InfoFragment())
            addFragment(RatesFragment())
            addFragment(ChatFragment())
        }
    }

    private fun setUpViewPager(adapter: PagerAdapter){
        binding.viewPager.apply {
            this.adapter = adapter
            offscreenPageLimit = adapter.itemCount//Para evitar que se destruyan los fragments y siempre tenerlos en memoria, no es recomendable si son varios elementos
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
                override fun onPageSelected(position: Int) {
                    if(prevBottomSelected == null){
                        binding.bottomNavigation.menu.getItem(0).isChecked = false
                    }else{
                        prevBottomSelected!!.isChecked = false
                    }
                    binding.bottomNavigation.menu.getItem(position).isChecked = true
                    prevBottomSelected = binding.bottomNavigation.menu.getItem(position)
                }
            })
        }
    }

    private fun setUpBottomNavigationBar(){
        binding.bottomNavigation.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.bottom_nav_info ->{
                    binding.viewPager.currentItem = 0
                    true
                }
                R.id.bottom_nav_rates ->{
                    binding.viewPager.currentItem = 1
                    true
                }
                R.id.bottom_nav_chat ->{
                    binding.viewPager.currentItem = 2
                    true
                }
                else -> false
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.general_options_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_log_out -> {
                FirebaseAuth.getInstance()
                    .signOut()
                goToActivity<LoginActivity>(){
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}