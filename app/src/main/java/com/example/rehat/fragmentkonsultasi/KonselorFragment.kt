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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_konselor, container, false)

        view.rvKonselor.setHasFixedSize(true)
        view.rvKonselor.layoutManager =  LinearLayoutManager(activity)

        for (i in listNama.indices){
            list.add(Konselor(listNama[i], listProfesi[i], listLokasi[i]))
            if(listNama.size - 1 == i){
                val adapter = Adapter(list)
                adapter.notifyDataSetChanged()
                view.rvKonselor.adapter = adapter
            }
        }

        return view
    }

}
