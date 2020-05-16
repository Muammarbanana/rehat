package com.example.rehat

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profil_konselor.*

class ProfilKonselor : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil_konselor)

        namaKonselor.text = intent.getStringExtra("Nama")
        textSpesialisasi.text = intent.getStringExtra("Spesialisasi")
        textLokasi.text = intent.getStringExtra("Lokasi")
        Picasso.get().load(intent.getStringExtra("Foto")).into(fotoKonselor)
        textBio.text = intent.getStringExtra("Bio")
        val id = intent.getStringExtra("Id")
        val address = intent.getStringExtra("Alamat")

        teksLihatMaps.setOnClickListener {
            openMap(address)
        }

        btnJanji.setOnClickListener {
            val intent = Intent(this, BuatJanji::class.java)
            intent.putExtra("Nama", namaKonselor.text.toString())
            intent.putExtra("Lokasi", textLokasi.text.toString())
            intent.putExtra("Id", id)
            intent.putExtra("Alamat", address)
            startActivity(intent)
        }
    }

    private fun openMap(address: String) {
        val address = "http://maps.google.co.in/maps?q=$address"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(address))
        this.startActivity(intent)
    }

    // Fungsi klik
    fun getBack(view: View) {
        finish()
    }
}
