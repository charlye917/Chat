package com.charlye934.chat.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter


class PagerAdapter(fm: FragmentActivity): FragmentStateAdapter(fm) {

    private val fragmentList = ArrayList<Fragment>()

    override fun getItemCount(): Int = fragmentList.size

    override fun createFragment(position: Int): Fragment = fragmentList[position]

    fun addFragment(fragment: Fragment) = fragmentList.add(fragment)

}