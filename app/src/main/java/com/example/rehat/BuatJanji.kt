package com.example.rehat

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
import kotlinx.android.synthetic.main.pop_alert.*
import kotlinx.android.synthetic.main.pop_alert.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class BuatJanji : AppCompatActivity() {

    private lateinit var ref : DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var id: String
    private lateinit var address: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buat_janji)
        auth = FirebaseAuth.getInstance()

        janjiNamaKonselor.text = intent.getStringExtra("Nama")
        lokasiJanji.text = intent.getStringExtra("Lokasi")
        id = intent.getStringExtra("Id")
        address = intent.getStringExtra("Alamat")

        rvHari.setHasFixedSize(true)
        rvHari.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        rvWaktu.setHasFixedSize(true)
        rvWaktu.layoutManager = GridLayoutManager(this, 4)

        btnBikinJanji.setOnClickListener { popAlert() }

        textLihatMaps.setOnClickListener { openMap(address) }

        getDataJadwal(id.toDouble())
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
                    daftarHari = ArrayList(daftarHari.distinct())
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
            val tanggal = c.get(Calendar.DAY_OF_WEEK)
            if (tanggal == hari) {
                finalDate = today
            }
            c.add(Calendar.DAY_OF_MONTH, 1)
        }
        return finalDate
    }

    private fun popAlert() {
        val dialog = AlertDialog.Builder(this).create()
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.pop_alert, null)
        dialog.setView(dialogView)
        dialog.setCancelable(true)
        dialogView.alertText.text = "Apakah kamu yakin ingin mengirim permohonan konsultasi? Pastikan kamu telah mengisi jadwal dengan benar"
        dialogView.btnCancel.setOnClickListener { dialog.dismiss() }
        dialogView.btnAccept.setOnClickListener {
            buatJanji()
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun openMap(address: String) {
        val address = "http://maps.google.co.in/maps?q=$address"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(address))
        this.startActivity(intent)
    }

    // Fungsi Klik
    private fun buatJanji() {
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
        val namadokter = janjiNamaKonselor.text.toString()
        ref.push().setValue(Janji(lokasiJanji.text.toString(),tanggal, jam, catatan, auth.currentUser?.uid!!, id, 0, namadokter, address))
        Toast.makeText(this, "Berhasil Membuat Janji Konsultasi", Toast.LENGTH_SHORT).show()
        var intent = Intent(this, Home::class.java)
        intent.putExtra("DataTabChat", "2")
        intent = intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent = intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }

    fun getBack(view: View) {
        finish()
    }
}
