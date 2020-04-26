package com.example.rehat

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_registrasi.*
import kotlinx.android.synthetic.main.activity_welcome_screen.*

class Registrasi : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrasi)

        backRegis.setOnClickListener { finish() }
        regisButton.setOnClickListener { Toast.makeText(this, "Pendaftaran berhasil. Silakan login", Toast.LENGTH_SHORT).show() }
    }
}
