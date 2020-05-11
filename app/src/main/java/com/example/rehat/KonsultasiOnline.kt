package com.example.rehat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_konsultasi_online.*

class KonsultasiOnline : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_konsultasi_online)

        ikonBackChat.setOnClickListener {
            finish()
        }
    }
}
