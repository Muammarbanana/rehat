package com.example.rehat

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_isi_materi.*

class IsiMateri : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_isi_materi)

        judulMateri.text = intent.getStringExtra("Judul")
        Picasso.get().load(intent.getStringExtra("Gambar")).resize(328,191).centerCrop().into(imgMateri)
        textIsi.text = intent.getStringExtra("Isi")
        val backgroundcolor = intent.getStringExtra("Warna")

        toolbar.background = ColorDrawable(Color.parseColor(backgroundcolor))
        val window = this.window
        window.statusBarColor = Color.parseColor(backgroundcolor)

    }

    fun getBack(view: View) { finish() }
}
