package com.example.rehat.fragmentkonsultasi

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.rehat.R
import com.example.rehat.viewmodel.SharedViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_persetujuan.view.*

/**
 * A simple [Fragment] subclass.
 */
class PersetujuanFragment : Fragment() {

    private lateinit var viewModel: SharedViewModel
    private lateinit var ref: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_persetujuan, container, false)
        //getDataJanji(inflater, container)

        viewModel = activity?.run {
            ViewModelProviders.of(this)[SharedViewModel::class.java]
        } ?: throw Exception("Invalid Activity")

        view.btnPilihKonselor.setOnClickListener {
            viewModel.selectedTab("go to tab pilih konselor")
        }

        return view
    }
}
