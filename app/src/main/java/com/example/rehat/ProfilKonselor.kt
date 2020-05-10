package com.example.rehat

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

        ikonBackKonselor.setOnClickListener {
            finish()
        }
    }
}
