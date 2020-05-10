package com.example.rehat.fragmenthome

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.rehat.PagerAdapterKonsultasi
import com.example.rehat.R
import com.example.rehat.SharedViewModel
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_konsultasi.view.*

/**
 * A simple [Fragment] subclass.
 */
class KonsultasiFragment : Fragment() {

    private lateinit var viewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_konsultasi, container, false)

        view.viewPager2.adapter = PagerAdapterKonsultasi(childFragmentManager)
        view.tabsKonsultasi.setupWithViewPager(view.viewPager2)

        viewModel = activity?.run {
            ViewModelProviders.of(this)[SharedViewModel::class.java]
        } ?: throw Exception("Invalid Activity")

        viewModel.selected.observeForever(androidx.lifecycle.Observer {
            if (it.equals("go to tab pilih konselor")) {
                view.tabsKonsultasi.getTabAt(0)?.select()
            }
        })

        return view
    }

}
