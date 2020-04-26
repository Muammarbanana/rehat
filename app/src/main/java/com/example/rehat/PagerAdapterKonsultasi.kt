package com.example.rehat

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.example.rehat.fragmentkonsultasi.KonselorFragment
import com.example.rehat.fragmentkonsultasi.PersetujuanFragment

class PagerAdapterKonsultasi(fm: FragmentManager): FragmentPagerAdapter(fm) {

    private val pages = listOf(
        KonselorFragment(),
        PersetujuanFragment()
    )

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