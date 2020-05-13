package com.example.rehat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.Sampler
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rehat.rvlisthari.Adapter
import com.example.rehat.rvlisthari.AdapterWaktu
import com.example.rehat.rvlisthari.Hari
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_buat_janji.*

class BuatJanji : AppCompatActivity() {

    val hari = listOf(
        "Senin",
        "Selasa",
        "Rabu"
    )

    val tanggal = listOf(
        "02 Mei",
        "03 Mei",
        "04 Mei"
    )

    val waktu = arrayListOf(
        "08.00",
        "12.00",
        "14.00",
        "16.00",
        "18.00"
    )

    private lateinit var ref : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buat_janji)

        janjiNamaKonselor.text = intent.getStringExtra("Nama")
        lokasiJanji.text = intent.getStringExtra("Lokasi")
        val id = intent.getStringExtra("Id")

        rvHari.setHasFixedSize(true)
        rvHari.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        /*val dataHari = arrayListOf<Hari>()

        for (h in 0..2) {
            dataHari.add(Hari(hari[h], tanggal[h]))
        }
        val adapter = Adapter(dataHari)
        adapter.notifyDataSetChanged()
        rvHari.adapter = adapter*/

        rvWaktu.setHasFixedSize(true)
        rvWaktu.layoutManager = GridLayoutManager(this, 4)

        /*val adapterwaktu = AdapterWaktu(waktu)
        adapterwaktu.notifyDataSetChanged()
        rvWaktu.adapter = adapterwaktu*/

        getDataJadwal(id.toDouble())

        ikonBackJanji.setOnClickListener {
            finish()
        }
    }

    private fun getDataJadwal(id: Double) {
        val daftarHari = arrayListOf<Hari>()
        val daftarJam = arrayListOf<String>()
        ref = FirebaseDatabase.getInstance().getReference("jadwal")
        ref.orderByChild("id_konselor").equalTo(id).addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    for (h in p0.children) {
                        val hari = h.child("hari").value.toString()
                        val jam = h.child("jam").value.toString()
                        daftarHari.add(Hari(hari, "nul"))
                        daftarJam.add(jam)
                    }
                    val adapter = Adapter(ArrayList(daftarHari.distinct()))
                    val adapterWaktu = AdapterWaktu(ArrayList(daftarJam.distinct()))
                    adapter.notifyDataSetChanged()
                    adapterWaktu.notifyDataSetChanged()
                    rvHari.adapter = adapter
                    rvWaktu.adapter = adapterWaktu
                }
            }
        })
    }
}
