package com.example.rehat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_konsultasi_online.*

class KonsultasiOnline : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_konsultasi_online)

        btnPilihKonselor.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            intent.putExtra("DataTabChat", "2")
            startActivity(intent)
            finish()
        }
    }

    // Fungsi klik
    fun getBack(view: View) {
        finish()
    }
}
