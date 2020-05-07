package com.example.rehat.fragmentkonsultasi

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.rehat.R
import com.example.rehat.rvlistkonselor.Adapter
import com.example.rehat.rvlistkonselor.Konselor
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_konselor.view.*
import java.util.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class KonselorFragment : Fragment() {

    private lateinit var ref: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_konselor, container, false)

        view.rvKonselor.setHasFixedSize(true)
        view.rvKonselor.layoutManager =  LinearLayoutManager(activity)

        getDataKonselor(view)

        return view
    }

    private fun getDataKonselor(view: View) {
        val daftarKonselor = arrayListOf<Konselor>()
        ref = FirebaseDatabase.getInstance().getReference("konselor")
        ref.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()) {
                    for(h in p0.children) {
                        val nama = h.child("nama_konselor").value.toString()
                        val tempat = h.child("tempat").value.toString()
                        val spesialis = h.child("spesialis").value.toString()
                        daftarKonselor.add(Konselor(nama, spesialis, tempat))
                    }
                    val adapter = Adapter(daftarKonselor)
                    adapter.notifyDataSetChanged()
                    view.rvKonselor.adapter = adapter
                }
            }
        })
    }
}
