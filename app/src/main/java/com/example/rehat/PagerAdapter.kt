package com.example.rehat

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.rehat.fragmenthome.*

class PagerAdapter(fm: FragmentManager, var pages: ArrayList<Fragment>): FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return pages[position]
    }

    override fun getCount(): Int {
        return pages.size
    }

    // judul untuk tabs
    override fun getPageTitle(position: Int): CharSequence? {
        return null
    }
}