package com.example.rehat

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
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