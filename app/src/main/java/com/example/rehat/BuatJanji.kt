package com.example.rehat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.AsyncListUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rehat.rvlisthari.Adapter
import com.example.rehat.rvlistwaktu.AdapterWaktu
import com.example.rehat.rvlisthari.Hari
import com.example.rehat.rvlistwaktu.Waktu
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_buat_janji.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class BuatJanji : AppCompatActivity() {

    private lateinit var ref : DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var id: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buat_janji)
        auth = FirebaseAuth.getInstance()

        janjiNamaKonselor.text = intent.getStringExtra("Nama")
        lokasiJanji.text = intent.getStringExtra("Lokasi")
        id = intent.getStringExtra("Id")

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
        var daftarHari = arrayListOf<Hari>()
        val daftarJam = arrayListOf<Waktu>()
        ref = FirebaseDatabase.getInstance().getReference("jadwal")
        ref.orderByChild("id_konselor").equalTo(id).addListenerForSingleValueEvent(object: ValueEventListener {
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
                    daftarHari = daftarHari.distinct() as ArrayList<Hari>
                    daftarHari.sortBy { it.tanggal }
                    val adapter = Adapter(ArrayList(daftarHari))
                    val adapterWaktu = AdapterWaktu(ArrayList(daftarJam.distinct()))
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
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val today = formatter.format(c.time)
            Log.d("Now", "${c.time}")
            val tanggal = c.get(Calendar.DAY_OF_WEEK)
            val namatanggal = c.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())
            Log.d("Tanggal", "$namatanggal")
            if (tanggal == hari) {
                finalDate = today
            }
            c.add(Calendar.DAY_OF_MONTH, 1)
        }
        return finalDate
    }

    // Fungsi Klik

    private fun buatJanji(view: View) {
        ref = FirebaseDatabase.getInstance().getReference("janji")
        val jum = rvHari.adapter?.itemCount
        val jmljam = rvWaktu.adapter?.itemCount
        var teks: Int; var tanggal = ""; var jam = ""
        for (i in 0 until jum!!) {
            teks = rvHari
                .findViewHolderForAdapterPosition(i)
                ?.itemView
                ?.findViewById<TextView>(R.id.teksHari)
                ?.currentTextColor!!
            if (teks == -1) {
                tanggal = rvHari
                    .findViewHolderForAdapterPosition(i)
                    ?.itemView
                    ?.findViewById<TextView>(R.id.teksTanggal)
                    ?.text.toString()
                break
            }
        }
        for (i in 0 until jmljam!!) {
            teks = rvWaktu
                .findViewHolderForAdapterPosition(i)
                ?.itemView
                ?.findViewById<TextView>(R.id.teksJam)
                ?.currentTextColor!!
            if (teks == -1) {
                jam = rvWaktu
                    .findViewHolderForAdapterPosition(i)
                    ?.itemView
                    ?.findViewById<TextView>(R.id.teksJam)
                    ?.text.toString()
            }
        }
        val catatan = editTextCatatan.text.toString()
        ref.push().setValue(Janji(lokasiJanji.text.toString(),tanggal, jam, catatan, auth.currentUser?.uid!!, id))
        Toast.makeText(this, "Berhasil Membuat Janji Konsultasi", Toast.LENGTH_SHORT).show()
        finish()
    }
}
