package com.example.rehat.fragmenthome

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import com.example.rehat.PagerAdapterKonsultasi
import com.example.rehat.R
import com.example.rehat.SharedViewModel
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

        Log.d("Cycle", "Ini onCreateView")

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
        var pages: ArrayList<Fragment>
        ref = FirebaseDatabase.getInstance().getReference("janji")
        ref.orderByChild("id_user").equalTo(auth.currentUser?.uid!!).addValueEventListener(object:
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
        Log.d("Cycle", "ini onAttach")
    }

    override fun onResume() {
        super.onResume()
        Log.d("Cycle", "Ini onResume")
        /*val manager = KonsultasiFragment().fragmentManager
        val trans = manager?.beginTransaction()
        trans?.remove(KonsultasiFragment())
        trans?.commit()*/
        //view?.let { getDataJanji(it) }
    }

    override fun onPause() {
        super.onPause()
        Log.d("Cycle", "Ini onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("Cycle", "Ini onStop")
    }

    override fun onStart() {
        super.onStart()
        Log.d("Cycle", "Ini onStart")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("Cycle", "Ini onDestroy")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d("Cycle", "Ini onActivityCreated")
    }
}
