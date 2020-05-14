package com.example.rehat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rehat.rvlisthari.Adapter
import com.example.rehat.rvlistwaktu.AdapterWaktu
import com.example.rehat.rvlisthari.Hari
import com.example.rehat.rvlistwaktu.Waktu
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_buat_janji.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

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

        rvWaktu.setHasFixedSize(true)
        rvWaktu.layoutManager = GridLayoutManager(this, 4)

        getDataJadwal(id.toDouble())

        ikonBackJanji.setOnClickListener {
            finish()
        }
    }

    private fun getDataJadwal(id: Double) {
        val daftarHari = arrayListOf<Hari>()
        val daftarJam = arrayListOf<Waktu>()
        ref = FirebaseDatabase.getInstance().getReference("jadwal")
        ref.orderByChild("id_konselor").equalTo(id).addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    for (h in p0.children) {
                        val hari = h.child("hari").value.toString()
                        val jam = h.child("jam").value.toString()
                        daftarHari.add(Hari(hari, getDate(hari.toInt()), 0))
                        daftarJam.add(Waktu(jam, 0))
                    }
                    val adapter = Adapter(ArrayList(daftarHari.distinct()))
                    val adapterWaktu =
                        AdapterWaktu(
                            ArrayList(daftarJam.distinct())
                        )
                    adapter.notifyDataSetChanged()
                    adapterWaktu.notifyDataSetChanged()
                    rvHari.adapter = adapter
                    rvWaktu.adapter = adapterWaktu
                }
            }
        })
    }

    private fun getDate(hari: Int): String {
        var finalDate = String()
        val sCalendar = Calendar.getInstance()
        sCalendar.add(Calendar.DATE,7)
        val c = GregorianCalendar()
        while (c.time.before(Date(sCalendar.timeInMillis))) {
            val formatter = SimpleDateFormat("dd/MM/yyyy")
            c.add(Calendar.DAY_OF_MONTH, 1)
            val today = formatter.format(c.time)
            val tanggal = c.get(Calendar.DAY_OF_WEEK)
            Log.d("Tanggal", "$tanggal")
            if (tanggal == hari) {
                finalDate = today
            }
        }
        return finalDate
    }
}
