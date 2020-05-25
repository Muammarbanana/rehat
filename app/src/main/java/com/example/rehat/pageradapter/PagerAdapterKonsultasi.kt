package com.example.rehat.pageradapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class PagerAdapterKonsultasi(fm: FragmentManager, var pages: ArrayList<Fragment>): FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return pages[position]
    }

    override fun getCount(): Int {
        return pages.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when(position){
            0 -> "Memilih Konselor"
            else -> "Status Persetujuan"
        }
    }

}