package com.example.rehat.fragmenthome

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.rehat.PagerAdapterKonsultasi
import com.example.rehat.R
import kotlinx.android.synthetic.main.fragment_konsultasi.view.*

/**
 * A simple [Fragment] subclass.
 */
class KonsultasiFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_konsultasi, container, false)

        view.viewPager2.adapter = PagerAdapterKonsultasi(childFragmentManager)
        view.tabsKonsultasi.setupWithViewPager(view.viewPager2)

        return view
    }

}
