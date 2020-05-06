package com.example.rehat.fragmenthome

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.rehat.R
import com.example.rehat.SharedViewModel
import kotlinx.android.synthetic.main.fragment_halaman_tersimpan.*
import kotlinx.android.synthetic.main.fragment_halaman_tersimpan.view.*

/**
 * A simple [Fragment] subclass.
 */
class HalamanTersimpanFragment : Fragment() {

    private lateinit var viewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_halaman_tersimpan, container, false)

        viewModel = activity?.run {
            ViewModelProviders.of(this)[SharedViewModel::class.java]
        } ?: throw Exception("Invalid Activity")

        view.btnBelum.setOnClickListener {
            viewModel.selectedTab("go to tab 1")
        }

        return view
    }
}
