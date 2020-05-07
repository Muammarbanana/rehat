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

    val list = ArrayList<Konselor>()
    val listNama = arrayOf(
        "Dr. Reza Rahmawan, Sp.KK",
        "Dr. Helena Christine, Sp.KJ",
        "Dr. Stephanie Putry, Sp.KJ",
        "Dr. Wander Luiz, Sp.KJ"
    )
    val listProfesi = arrayOf(
        "Spesialis Kulit dan Kelamin",
        "Spesialis Jiwa dan Psikiatri",
        "Spesialis Jiwa dan Psikiatri",
        "Spesialis Jiwa dan Psikiatri"
    )

    val listLokasi = arrayOf(
        "Rumah Cemara",
        "Klinik Medika",
        "Klinik Anahata",
        "Klinik Brawijawa"
    )

    private lateinit var ref: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_konselor, container, false)

        view.rvKonselor.setHasFixedSize(true)
        view.rvKonselor.layoutManager =  LinearLayoutManager(activity)

        /*for (i in listNama.indices){
            list.add(Konselor(listNama[i], listProfesi[i], listLokasi[i]))
            if(listNama.size - 1 == i){
                val adapter = Adapter(list)
                adapter.notifyDataSetChanged()
                view.rvKonselor.adapter = adapter
            }
        }*/
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
