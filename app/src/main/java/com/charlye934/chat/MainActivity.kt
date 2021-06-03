package com.charlye934.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.view.get
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.charlye934.chat.adapters.PagerAdapter
import com.charlye934.chat.fragment.ChatFragment
import com.charlye934.chat.fragment.InfoFragment
import com.charlye934.chat.fragment.RatesFragment
import com.example.chat.R
import com.example.chat.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var prevBottomSelected: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpViewPager(getPagerAdapter())
        setUpBottomNavigationBar()

    }

    private fun setUpViewPager(adapter: PagerAdapter){
        binding.viewPager.apply {
            this.adapter = adapter
            offscreenPageLimit = adapter.itemCount
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

    private fun getPagerAdapter(): PagerAdapter {
        var adapter = PagerAdapter(this)
        return adapter.apply {
            addFragment(InfoFragment())
            addFragment(RatesFragment())
            addFragment(ChatFragment())
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
        menuInflater.inflate(R.menu.bottom_navigation_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}