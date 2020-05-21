package com.example.rehat

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_isi_materi.*

class IsiMateri : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_isi_materi)

        judulMateri.text = intent.getStringExtra("Judul")
        Picasso.get().load(intent.getStringExtra("Gambar")).resize(328,191).centerCrop().into(imgMateri)
        val htmlString = intent.getStringExtra("Isi")
        imgMateri.contentDescription = intent.getStringExtra("Desk")
        val backgroundcolor = intent.getStringExtra("Warna")

        webView.loadUrl(htmlString)

        toolbar.background = ColorDrawable(Color.parseColor(backgroundcolor))
        val window = this.window
        window.statusBarColor = Color.parseColor(backgroundcolor)

    }

    fun getBack(view: View) { finish() }

    fun shareMateri(view: View) {
        val text = judulMateri.text
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "$text \n\nBaca materi selengkapnya di https://drive.google.com/open?id=1C9dlCqQYjwHadR59MI9i9FKCQWWwE-j_")
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }
}
