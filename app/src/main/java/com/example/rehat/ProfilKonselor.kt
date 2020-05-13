package com.example.rehat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

        ikonBackKonselor.setOnClickListener {
            finish()
        }

        btnJanji.setOnClickListener {
            val intent = Intent(this, BuatJanji::class.java)
            intent.putExtra("Nama", namaKonselor.text.toString())
            intent.putExtra("Lokasi", textLokasi.text.toString())
            intent.putExtra("Id", id)
            startActivity(intent)
        }
    }
}
