package com.example.rehat

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_rincian_persetujuan.*
import kotlin.math.roundToInt

class RincianPersetujuan : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rincian_persetujuan)

        val idJanji = intent.getStringExtra("idJanji")

        getDataJanji(idJanji)

        imgBackRincian.setOnClickListener { finish() }

    }

    private fun getDataJanji(id: String) {
        val ref = FirebaseDatabase.getInstance().getReference("janji")
        ref.orderByKey().equalTo(id).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            @SuppressLint("MissingPermission")
            override fun onDataChange(p0: DataSnapshot) {
                for (h in p0.children) {
                    val namakonselor = h.child("namadokter").value.toString()
                    val idkonselor = h.child("id_konselor").value.toString()
                    val iduser = h.child("id_user").value.toString()
                    val pesan = h.child("pesan").value.toString()
                    val address = h.child("alamat").value.toString()
                    val status = h.child("status").value.toString()
                    val tempat = h.child("tempat").value.toString()
                    rincianNamaKonselor.text = namakonselor
                    rincianPesan.text = pesan
                    getNamaUser(iduser)
                    if (status.toInt() == 2) {
                        titleRincian.text = "Alasan Penolakan"
                        btnAturUlang.visibility = View.VISIBLE
                        btnLihatMaps.text = "Memilih Konselor Lain"
                        btnLihatMaps.setOnClickListener {
                            val intent = Intent(this@RincianPersetujuan, Home::class.java)
                            intent.putExtra("DataTabChat", "2")
                            startActivity(intent)
                            finish()
                        }
                        btnAturUlang.setOnClickListener {
                            val loc = getLocFromAdrres(address)
                            val locmanager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
                            val userloc = locmanager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
                            //val distance = getDistance(userloc.latitude, userloc.longitude, loc[0], loc[1])
                            val kmdistance: Float
                            if (userloc != null) {
                                val distance = FloatArray(1)
                                Location.distanceBetween(userloc.latitude, userloc.longitude, loc[0], loc[1], distance)
                                kmdistance = distance[0] / 1000
                            } else {
                                kmdistance = 0F
                            }
                            val intent = Intent(this@RincianPersetujuan, BuatJanji::class.java)
                            intent.putExtra("Nama", namakonselor)
                            intent.putExtra("Lokasi", tempat)
                            intent.putExtra("Id", idkonselor)
                            intent.putExtra("Alamat", address)
                            intent.putExtra("Jarak", kmdistance.roundToInt().toString())
                            startActivity(intent)
                        }
                    } else {
                        btnLihatMaps.setOnClickListener {
                            openMap(address)
                        }
                    }
                }
            }
        })
    }

    private fun getNamaUser(id: String) {
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.orderByKey().equalTo(id).addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
                for (h in p0.children) {
                    val nama = h.child("nama").value.toString()
                    rincianNama.text = "Yth. $nama"
                }
            }
        })
    }

    private fun openMap(address: String) {
        val address = "http://maps.google.co.in/maps?q=$address"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(address))
        this.startActivity(intent)
    }

    fun getLocFromAdrres(address: String): ArrayList<Double> {
        var coder = Geocoder(this)
        var straddress = coder.getFromLocationName(address, 1)
        var location = straddress[0]
        var latlong = ArrayList<Double>()
        latlong.add(location.latitude)
        latlong.add(location.longitude)
        return latlong
    }

}
