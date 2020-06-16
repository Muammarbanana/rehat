package com.example.rehat

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rehat.rvlistsubmateri.AdapterSub
import com.example.rehat.rvlistsubmateri.SubMateri
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_isi_materi.*

class IsiMateri : AppCompatActivity() {

    private var position = -1
    private var daftarjudul = arrayListOf<String>()
    private var daftargambar = arrayListOf<String>()
    private var daftarisi = arrayListOf<String>()
    private var daftardesk = arrayListOf<String>()
    private lateinit var htmlString: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_isi_materi)

        val backgroundcolor = intent.getStringExtra("Warna")
        daftarjudul = intent.getStringArrayListExtra("DaftarJudul")
        daftargambar = intent.getStringArrayListExtra("DaftarGambar")
        daftarisi = intent.getStringArrayListExtra("DaftarIsi")
        daftardesk = intent.getStringArrayListExtra("DaftarDesk")
        val slider = intent.getIntExtra("Slider", 0)
        position = intent.getStringExtra("Position").toInt()

        getMateriContent(position)

        toolbar.background = ColorDrawable(Color.parseColor(backgroundcolor))
        val window = this.window
        window.statusBarColor = Color.parseColor(backgroundcolor)

        backToTop.setOnClickListener {
            scrollMateri.fullScroll(NestedScrollView.FOCUS_UP)
        }

        if (position == daftarjudul.size - 1 && position == 0) {
            ikonNextMateri.visibility = View.INVISIBLE
            ikonPrevMateri.visibility = View.INVISIBLE
        } else if (position == daftarjudul.size - 1) {
            ikonNextMateri.visibility = View.INVISIBLE
        } else if (position == 0) {
            ikonPrevMateri.visibility = View.INVISIBLE
        } else {
            ikonNextMateri.visibility = View.VISIBLE
            ikonPrevMateri.visibility = View.VISIBLE
        }

        ikonNextMateri.setOnClickListener {
            if (position == daftarjudul.size - 2) {
                position += 1
                getMateriContent(position)
                ikonNextMateri.visibility = View.INVISIBLE
                ikonPrevMateri.visibility = View.VISIBLE
            } else {
                position += 1
                getMateriContent(position)
                ikonNextMateri.visibility = View.VISIBLE
                ikonPrevMateri.visibility = View.VISIBLE
            }
        }

        ikonPrevMateri.setOnClickListener {
            if (position == 1) {
                position -= 1
                getMateriContent(position)
                ikonNextMateri.visibility = View.VISIBLE
                ikonPrevMateri.visibility = View.INVISIBLE
            } else {
                position -= 1
                getMateriContent(position)
                ikonNextMateri.visibility = View.VISIBLE
                ikonPrevMateri.visibility = View.VISIBLE
            }
        }

        if (slider == 0) {
            ikonNextMateri.visibility = View.INVISIBLE
            ikonPrevMateri.visibility = View.INVISIBLE
        }
    }

    fun getBack(view: View) { finish() }

    fun shareMateri(view: View) {
        val text = judulMateri.text
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "$text \n\nBaca materi selengkapnya di bit.ly/rehatofficialapp")
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    fun getMateriContent(position: Int) {
        judulMateri.text = daftarjudul[position]
        Picasso.get().load(daftargambar[position]).resize(328,191).centerCrop().into(imgMateri)
        htmlString = daftarisi[position]
        imgMateri.contentDescription = daftardesk[position]
        //webView.loadUrl(htmlString)
        webView.loadData(htmlString, "text/html", "UTF-8")
    }
}
