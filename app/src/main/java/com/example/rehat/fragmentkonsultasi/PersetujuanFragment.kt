package com.example.rehat.fragmentkonsultasi

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.rehat.R
import com.example.rehat.SharedViewModel
import kotlinx.android.synthetic.main.fragment_halaman_tersimpan.view.*
import kotlinx.android.synthetic.main.fragment_persetujuan.view.*

/**
 * A simple [Fragment] subclass.
 */
class PersetujuanFragment : Fragment() {

    private lateinit var viewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_persetujuan, container, false)

        viewModel = activity?.run {
            ViewModelProviders.of(this)[SharedViewModel::class.java]
        } ?: throw Exception("Invalid Activity")

        view.btnPilihKonselor.setOnClickListener {
            viewModel.selectedTab("go to tab pilih konselor")
        }

        return view
    }

}
