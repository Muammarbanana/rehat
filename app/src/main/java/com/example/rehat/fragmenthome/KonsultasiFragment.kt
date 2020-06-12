package com.example.rehat.fragmenthome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.rehat.pageradapter.PagerAdapterKonsultasi
import com.example.rehat.R
import com.example.rehat.viewmodel.SharedViewModel
import com.example.rehat.fragmentkonsultasi.KonselorFragment
import com.example.rehat.fragmentkonsultasi.PersetujuanFragment
import com.example.rehat.fragmentkonsultasi.PersetujuanIsiFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_konsultasi.view.*

/**
 * A simple [Fragment] subclass.
 */
class KonsultasiFragment : Fragment() {

    private lateinit var viewModel: SharedViewModel
    private lateinit var ref: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_konsultasi, container, false)

        auth = FirebaseAuth.getInstance()

        viewModel = activity?.run {
            ViewModelProviders.of(this)[SharedViewModel::class.java]
        } ?: throw Exception("Invalid Activity")

        viewModel.selected.observeForever(androidx.lifecycle.Observer {
            if (it.equals("go to tab pilih konselor")) {
                view.tabsKonsultasi.getTabAt(0)?.select()
            } else if (it.equals("ke halaman persetujuan")) {
                view.tabsKonsultasi.getTabAt(1)?.select()
            }
        })

        val pages = arrayListOf(KonselorFragment(), PersetujuanFragment())

        view.viewPager2.adapter =
            PagerAdapterKonsultasi(
                childFragmentManager,
                pages
            )
        view.tabsKonsultasi.setupWithViewPager(view.viewPager2)

        return view
    }
}
