package com.example.rehat

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.rehat.model.Janji
import com.example.rehat.roomdb.RoomDB
import com.example.rehat.rvlisthari.Adapter
import com.example.rehat.rvlistwaktu.AdapterWaktu
import com.example.rehat.rvlisthari.Hari
import com.example.rehat.rvlistwaktu.Waktu
import com.example.rehat.viewmodel.SharedViewModel
import com.example.rehat.viewmodel.ViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_buat_janji.*
import kotlinx.android.synthetic.main.pop_alert.view.*
import kotlinx.android.synthetic.main.toast_layout.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class BuatJanji : AppCompatActivity() {

    private lateinit var ref : DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var id: String
    private lateinit var address: String
    private lateinit var viewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buat_janji)
        auth = FirebaseAuth.getInstance()

        janjiNamaKonselor.text = intent.getStringExtra("Nama")
        lokasiJanji.text = intent.getStringExtra("Lokasi")
        teksJarak.text = intent.getStringExtra("Jarak") + " km dari lokasi kamu"
        id = intent.getStringExtra("Id")
        address = intent.getStringExtra("Alamat")

        val roomDB = Room.databaseBuilder(this, RoomDB::class.java, "materiDB").allowMainThreadQueries().build()

        viewModel = ViewModelProviders.of(this,
            ViewModelFactory(roomDB!!.materiDao())
        )[SharedViewModel::class.java]

        viewModel.selected.observeForever{
            when(it) {
                "updatelistjam" -> getDataJamFiltered(id)
            }
        }

        rvHari.setHasFixedSize(true)
        rvHari.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        rvWaktu.setHasFixedSize(true)
        rvWaktu.layoutManager = GridLayoutManager(this, 4)

        btnBikinJanji.setOnClickListener { popAlert() }

        textLihatMaps.setOnClickListener { openMap(address) }

        getDataJadwal(id)
    }

    private fun getDataJadwal(id: String) {
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
                    val adapter = Adapter(ArrayList(daftarHari), viewModel)
                    val adapterWaktu = AdapterWaktu(ArrayList(daftarJam.distinct()))
                    adapter.notifyDataSetChanged()
                    adapterWaktu.notifyDataSetChanged()
                    rvHari.adapter = adapter
                    rvWaktu.adapter = adapterWaktu
                }
            }
        })
    }

    private fun getDataJamFiltered(id: String) {
        val listJanji = arrayListOf<Janji>()
        val listJadwal = arrayListOf<Waktu>()
        ref = FirebaseDatabase.getInstance().getReference("janji")
        ref.orderByChild("id_konselor").equalTo(id).addValueEventListener(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                listJanji.clear()
                p0.children.forEach {h ->
                    val idKonselor = h.child("id_konselor").value.toString()
                    val idUser = h.child("id_user").value.toString()
                    val status = h.child("status").value.toString()
                    val jam = h.child("jam").value.toString()
                    val tanggal = h.child("tanggal").value.toString()
                    val catatan = h.child("catatan").value.toString()
                    val tempat = h.child("tempat").value.toString()
                    val namadokter = h.child("namadokter").value.toString()
                    val alamat = h.child("alamat").value.toString()
                    listJanji.add(Janji(tempat, tanggal, jam, catatan, idUser, idKonselor, status.toInt(), namadokter, alamat))
                }
                val ref2 = FirebaseDatabase.getInstance().getReference("jadwal")
                ref2.orderByChild("id_konselor").equalTo(id).addListenerForSingleValueEvent(object: ValueEventListener{
                    override fun onCancelled(p1: DatabaseError) {  }

                    override fun onDataChange(p1: DataSnapshot) {
                        listJadwal.clear()
                        val jum = rvHari.adapter?.itemCount
                        var teks: Int; var hari = ""; var tanggal = ""
                        for (i in 0 until jum!!) {
                            if ( rvHari.findViewHolderForAdapterPosition(i) != null) {
                                teks = rvHari
                                    .findViewHolderForAdapterPosition(i)
                                    ?.itemView
                                    ?.findViewById<TextView>(R.id.teksHari)
                                    ?.currentTextColor!!
                                if (teks == -1) {
                                    hari = rvHari
                                        .findViewHolderForAdapterPosition(i)
                                        ?.itemView
                                        ?.findViewById<TextView>(R.id.teksHari)
                                        ?.text.toString()
                                    tanggal = rvHari
                                        .findViewHolderForAdapterPosition(i)
                                        ?.itemView
                                        ?.findViewById<TextView>(R.id.teksTanggal)
                                        ?.text.toString()
                                    break
                                }
                            }
                        }
                        if (p1.exists()) {
                            for (h in p1.children) {
                                val clock = h.child("jam").value.toString()
                                val day = h.child("hari").value.toString()
                                if (hari == toDay(day.toInt())) {
                                    listJadwal.add(getWaktu(tanggal, clock, listJanji))
                                }
                            }
                            val adapter = AdapterWaktu(listJadwal)
                            adapter.notifyDataSetChanged()
                            rvWaktu.adapter = adapter
                        }
                    }
                })
            }

        })

    }

    private fun getWaktu(tanggal: String, clock: String, listJanji: ArrayList<Janji>): Waktu {
        var waktu = Waktu(clock, 0)
        for (counter in 0 until listJanji.size) {
            if (tanggal == listJanji[counter].tanggal && "$clock.00" == listJanji[counter].jam) {
                waktu = Waktu(clock, 2)
                break
            }
            waktu = Waktu(clock, 0)
        }
        return waktu
    }

    private fun toDay(value: Int): String {
        var hari : String
        when (value) {
            1 -> hari = "Minggu"
            2 -> hari = "Senin"
            3 -> hari = "Selasa"
            4 -> hari = "Rabu"
            5 -> hari = "Kamis"
            6 -> hari = "Jumat"
            else -> hari = "Sabtu"
        }
        return hari
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
        ref.orderByChild("id_user").equalTo(auth.currentUser?.uid!!).addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    for (h in p0.children) {
                        val key = h.key.toString()
                        ref.child(key).child("tempat").setValue(lokasiJanji.text.toString())
                        ref.child(key).child("tanggal").setValue(tanggal)
                        ref.child(key).child("jam").setValue(jam)
                        ref.child(key).child("catatan").setValue(catatan)
                        ref.child(key).child("id_user").setValue(auth.currentUser?.uid!!)
                        ref.child(key).child("id_konselor").setValue(id)
                        ref.child(key).child("status").setValue(0)
                        ref.child(key).child("namadokter").setValue(namadokter)
                        ref.child(key).child("alamat").setValue(address)
                        ref.child(key).child("pesan").removeValue()
                    }
                } else {
                    ref.push().setValue(
                        Janji(
                            lokasiJanji.text.toString(),
                            tanggal,
                            jam,
                            catatan,
                            auth.currentUser?.uid!!,
                            id,
                            0,
                            namadokter,
                            address
                        )
                    )
                }
            }

        })
        val toastLayout = layoutInflater.inflate(R.layout.toast_layout, findViewById(R.id.constToast))
        val toast = Toast(this)
        toastLayout.textToast.text = "Permohonan konsultasi berhasil dikirim"
        toast.duration = Toast.LENGTH_SHORT
        toast.view = toastLayout
        toast.show()
        var intent = Intent(this, Home::class.java)
        intent.putExtra("DataTabChat", "9")
        intent = intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent = intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }

    fun getBack(view: View) {
        finish()
    }
}
