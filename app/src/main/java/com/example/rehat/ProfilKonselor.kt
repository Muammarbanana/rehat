package com.example.rehat

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.Sampler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.rehat.rvlisthari.Hari
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profil_konselor.*
import kotlinx.android.synthetic.main.pop_alert_single.view.*

class ProfilKonselor : AppCompatActivity() {

    private lateinit var ref: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var id: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil_konselor)

        namaKonselor.text = intent.getStringExtra("Nama")
        textSpesialisasi.text = intent.getStringExtra("Spesialisasi")
        textLokasi.text = intent.getStringExtra("Lokasi")
        Picasso.get().load(intent.getStringExtra("Foto")).into(fotoKonselor)
        textBio.text = intent.getStringExtra("Bio")
        id = intent.getStringExtra("Id")
        val address = intent.getStringExtra("Alamat")
        auth = FirebaseAuth.getInstance()

        teksLihatMaps.setOnClickListener { openMap(address) }

        btnJanji.setOnClickListener {
            val intent = Intent(this, BuatJanji::class.java)
            intent.putExtra("Nama", namaKonselor.text.toString())
            intent.putExtra("Lokasi", textLokasi.text.toString())
            intent.putExtra("Id", id)
            intent.putExtra("Alamat", address)
            startActivity(intent)
        }

        checkPromise()
        getDataHari(id.toDouble())
        getName()
    }

    private fun openMap(address: String) {
        val address = "http://maps.google.co.in/maps?q=$address"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(address))
        this.startActivity(intent)
    }

    private fun checkPromise() {
        ref = FirebaseDatabase.getInstance().getReference("janji")
        ref.orderByChild("id_user").equalTo(auth.currentUser?.uid!!).addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    btnJanji.setOnClickListener { popAlert() }
                }
            }
        })
    }

    private fun getName() {
        ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.orderByKey().equalTo(auth.currentUser?.uid!!).addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    for (h in p0.children) {
                        val name = h.child("nama").value.toString()
                        konsHei.text = "Hai $name,"
                    }
                }
            }
        })
    }

    private fun popAlert() {
        val dialog = AlertDialog.Builder(this).create()
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.pop_alert_single, null)
        dialog.setView(dialogView)
        dialog.setCancelable(true)
        dialogView.alertText.text = "Kamu tidak bisa membuat janji pertemuan lebih dari satu. Mohon selesaikan dulu pertemuanmu dengan Konselor"
        dialogView.btnAccept.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun getDataHari(id: Double) {
        var daftarHari = arrayListOf<String>()
        ref = FirebaseDatabase.getInstance().getReference("jadwal")
        ref.orderByChild("id_konselor").equalTo(id).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    for (h in p0.children) {
                        val hari = h.child("hari").value.toString()
                        daftarHari.add(hari)
                    }
                    daftarHari = ArrayList(daftarHari.distinct())
                    var listhari = ""
                    for (h in 0 until daftarHari.size) {
                        listhari += toDay(daftarHari[h].toInt())
                        if (h != daftarHari.size-1) {
                            listhari += " - "
                        }
                    }
                    teksDaftarHari.text = listhari
                }
            }
        })
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

    // Fungsi klik
    fun getBack(view: View) {
        val intent = Intent(this, Home::class.java)
        intent.putExtra("DataTabChat", "2")
        startActivity(intent)
        finish()
    }
}
