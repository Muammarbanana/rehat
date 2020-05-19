package com.example.rehat

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rehat.rvlistsubmateri.Adapter
import com.example.rehat.rvlistsubmateri.SubMateri
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_sub_materi.*

class SubMateri : AppCompatActivity() {

    private lateinit var ref: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub_materi)

        rvSubMateri.setHasFixedSize(true)
        rvSubMateri.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)

        val id = intent.getStringExtra("Id_materi")
        val background = intent.getStringExtra("Background").toInt()
        val backgroundcolor = getBackgroundColor(background)
        judulMateri.text = intent.getStringExtra("Judul")

        toolbar.background = ColorDrawable(Color.parseColor(backgroundcolor))
        val window = this.window
        window.statusBarColor = Color.parseColor(backgroundcolor)

        getDataSubMateri(id.toDouble(), backgroundcolor)

    }

    private fun getDataSubMateri(id: Double, color: String) {
        val daftarSub = arrayListOf<SubMateri>()
        ref  = FirebaseDatabase.getInstance().getReference("submateri")
        ref.orderByChild("materi_id").equalTo(id).addValueEventListener(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    for (h in p0.children) {
                        val judul = h.child("judul").value.toString()
                        val gambar = h.child("src_img").value.toString()
                        val jenis = h.child("jenis").value.toString()
                        val isi = h.child("isi").value.toString()
                        val deskripsigambar = h.child("img_desc").value.toString()
                        daftarSub.add(SubMateri(judul, gambar, jenis, isi, color, deskripsigambar))
                    }
                    val adapter = Adapter(daftarSub)
                    adapter.notifyDataSetChanged()
                    rvSubMateri.adapter = adapter
                }
            }
        })
    }

    private fun getBackgroundColor(num: Int): String {
        when (num) {
            1 -> return resources.getString(0+R.color.colorBlue)
            2 -> return resources.getString(0+R.color.colorPrimary)
            3 -> return resources.getString(0+R.color.colorPurple)
            4 -> return resources.getString(0+R.color.colorLightGreen)
            else -> return resources.getString(0+R.color.colorPink)
        }
    }

    fun getBack(view: View) { finish() }
}
