package com.example.rehat.fragmenthome

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.rehat.R
import com.example.rehat.rvlistmateri.Adapter
import com.example.rehat.rvlistmateri.Materi
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_edukasi.view.*
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class EdukasiFragment : Fragment() {

    private lateinit var ref: DatabaseReference

    val list = ArrayList<Materi>()
    val listMateri = arrayOf(
        "Chapter 1: Apa itu HIV & AIDS",
        "Chapter 2: Gejala dan Komplikasi",
        "Chapter 3: Pencegahan Virus HIV & AIDS",
        "Chapter 4: Tes HIV",
        "Chapter 5: Hak dan Kewajiban Pasien"
    )
    val listSubJudul = arrayOf(
        "3 Materi",
        "2 Materi",
        "2 Materi",
        "3 Materi",
        "3 Materi"
    )

    val listWarna = arrayOf(
        R.color.colorBlue,
        R.color.colorPrimary,
        R.color.colorPurple,
        R.color.colorLightGreen,
        R.color.colorPink
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_edukasi, container, false)

        view.rvMateri.setHasFixedSize(true)
        view.rvMateri.layoutManager =  LinearLayoutManager(activity)

        for (i in listMateri.indices){
            list.add(Materi(listMateri[i], listSubJudul[i], listWarna[i]))
            if(listMateri.size - 1 == i){
                val adapter = Adapter(list)
                adapter.notifyDataSetChanged()
                view.rvMateri.adapter = adapter
            }
        }

        return view
    }

    private fun getDataMateri() {
        val daftarJudul = mutableListOf<String>()
        val jumlahSub = mutableListOf<Int>()
        ref = FirebaseDatabase.getInstance().getReference("Materi")
        ref.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()) {
                    for(h in p0.children) {
                        val judul = h.child("judul").value.toString()
                        daftarJudul.add(judul)
                    }

                }
            }

        })
    }
}
