package com.example.rehat.fragmenthome

import android.content.Context
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.rehat.PagerAdapterKonsultasi
import com.example.rehat.R
import com.example.rehat.SharedViewModel
import com.example.rehat.fragmentkonsultasi.KonselorFragment
import com.example.rehat.fragmentkonsultasi.PersetujuanFragment
import com.example.rehat.fragmentkonsultasi.PersetujuanIsiFragment
import com.example.rehat.rvlistkonselor.Konselor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_konsultasi.view.*
import kotlinx.android.synthetic.main.fragment_persetujuan.view.*

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

        getDataJanji(view)

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

    private fun getDataJanji(view: View) {
        var pages = ArrayList<Fragment>()
        ref = FirebaseDatabase.getInstance().getReference("janji")
        ref.orderByChild("id_user").equalTo(auth.currentUser?.uid!!).addListenerForSingleValueEvent(object:
            ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    pages = arrayListOf(KonselorFragment(), PersetujuanIsiFragment())
                } else {
                    pages = arrayListOf(KonselorFragment(), PersetujuanFragment())
                }
                view.viewPager2.adapter = PagerAdapterKonsultasi(childFragmentManager, pages)
                view.tabsKonsultasi.setupWithViewPager(view.viewPager2)
            }
        })
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        view?.let { getDataJanji(it) }
    }
}
