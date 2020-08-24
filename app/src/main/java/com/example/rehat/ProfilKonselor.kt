package com.example.rehat

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profil_konselor.*
import kotlinx.android.synthetic.main.pop_alert.view.*
import kotlinx.android.synthetic.main.pop_alert_single.view.*
import kotlinx.android.synthetic.main.pop_alert_single.view.alertText
import kotlinx.android.synthetic.main.pop_alert_single.view.btnAccept
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.ProtocolException
import java.net.URL
import kotlin.math.roundToInt


class ProfilKonselor : AppCompatActivity() {

    private lateinit var ref: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var id: String

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil_konselor)

    }

    private fun openMap(address: String) {
        val address = "http://maps.google.co.in/maps?q=$address"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(address))
        this.startActivity(intent)
    }

    private fun checkPromise(address: String, kmdistance: String) {
        ref = FirebaseDatabase.getInstance().getReference("janji")
        ref.orderByChild("id_user").equalTo(auth.currentUser?.uid!!).addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    for (h in p0.children) {
                        val status = h.child("status").value.toString()
                        if (status.toInt() == 1 || status.toInt() == 0) {
                            btnJanji.setOnClickListener { popAlert("Kamu tidak bisa membuat janji pertemuan lebih dari satu. Mohon selesaikan dulu pertemuanmu dengan Konselor") }
                        } else {
                            btnJanji.setOnClickListener {
                                val intent = Intent(this@ProfilKonselor, BuatJanji::class.java)
                                intent.putExtra("Nama", namaKonselor.text.toString())
                                intent.putExtra("Lokasi", textLokasi.text.toString())
                                intent.putExtra("Id", id)
                                intent.putExtra("Alamat", address)
                                intent.putExtra("Jarak", kmdistance)
                                startActivity(intent)
                            }
                        }
                    }
                }
            }
        })
    }

    private fun checkChatKonsul() {
        ref = FirebaseDatabase.getInstance().getReference("messages")
        ref.orderByKey().equalTo(auth.currentUser?.uid!!).addValueEventListener(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    p0.children.forEach {p1 ->
                        p1.children.forEach {
                            if (it.key.toString() == id) {
                                btnKonsul.setOnClickListener {
                                    val intent = Intent(this@ProfilKonselor, ChatKonsultasi::class.java)
                                    intent.putExtra("Nama", namaKonselor.text.toString())
                                    intent.putExtra("Id", id)
                                    startActivity(intent)
                                }
                            } else {
                                btnKonsul.setOnClickListener {
                                    popAlert("Kamu tidak bisa konsultasi dengan Konselor lain secara bersamaan. Mohon selesaikan dulu konsultasi onlinemu")
                                }
                                btnJanji.setOnClickListener {
                                    popAlert("Kamu tidak bisa membuat janji pertemuan lebih dari satu. Mohon selesaikan dulu pertemuanmu dengan Konselor")
                                }
                            }
                        }
                    }
                }
            }
        })
    }

    private fun getName() {
        ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.orderByKey().equalTo(auth.currentUser?.uid!!).addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    for (h in p0.children) {
                        val name = h.child("nama").value.toString()
                        konsHei.text = "Hai $name,"
                    }
                }
            }
        })
    }

    private fun popAlert(teks: String) {
        val dialog = AlertDialog.Builder(this).create()
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.pop_alert_single, null)
        dialog.setView(dialogView)
        dialog.setCancelable(true)
        dialogView.alertText.text = teks
        dialogView.btnAccept.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun popAlertLocation(teks: String) {
        val dialog = AlertDialog.Builder(this).create()
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.pop_alert, null)
        dialog.setView(dialogView)
        dialog.setCancelable(false)
        dialogView.btnAccept.text = "Izinkan"
        dialogView.btnCancel.text = "Tolak"
        dialogView.alertText.text = teks
        dialogView.btnAccept.setOnClickListener {
            val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse(
                "package:$packageName"
            ))
            startActivity(intent)
            dialog.dismiss()
        }
        dialogView.btnCancel.setOnClickListener {
            dialog.dismiss()
            finish()
        }
        dialog.show()
    }

    private fun getDataHari(id: String) {
        var daftarHari = arrayListOf<String>()
        ref = FirebaseDatabase.getInstance().getReference("jadwal")
        ref.orderByChild("id_konselor").equalTo(id).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    for (h in p0.children) {
                        val hari = h.child("hari").value.toString()
                        daftarHari.add(hari)
                    }
                    daftarHari = ArrayList(daftarHari.distinct())
                    var listhari = ""
                    for (h in 0 until daftarHari.size) {
                        listhari += toDay(daftarHari[h].toInt())
                        if (h != daftarHari.size-1) {
                            listhari += " - "
                        }
                    }
                    teksDaftarHari.text = listhari
                }
            }
        })
    }

    private fun toDay(value: Int): String {
        var hari : String
        when (value) {
            1 -> hari = "Minggu"
            2 -> hari = "Senin"
            3 -> hari = "Selasa"
            4 -> hari = "Rabu"
            5 -> hari = "Kamis"
            6 -> hari = "Jumat"
            else -> hari = "Sabtu"
        }
        return hari
    }

    // Fungsi klik
    fun getBack(view: View) {
        val intent = Intent(this, Home::class.java)
        intent.putExtra("DataTabChat", "2")
        startActivity(intent)
        finish()
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

    override fun onResume() {
        super.onResume()
        namaKonselor.text = intent.getStringExtra("Nama")
        textSpesialisasi.text = intent.getStringExtra("Spesialisasi")
        textLokasi.text = intent.getStringExtra("Lokasi")
        Picasso.get().load(intent.getStringExtra("Foto")).resize(180, 200).into(fotoKonselor)
        textBio.text = intent.getStringExtra("Bio")
        id = intent.getStringExtra("Id")
        val address = intent.getStringExtra("Alamat")

        if ( Build.VERSION.SDK_INT >= 23 &&
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED){
            val loc = getLocFromAdrres(address)
            val locmanager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val userloc = locmanager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
            val kmdistance: Float
            if (userloc != null) {
                var distance = FloatArray(1)
                Location.distanceBetween(userloc.latitude, userloc.longitude, loc[0], loc[1], distance)
                kmdistance = distance[0] / 1000
                teksJarak.text = kmdistance.roundToInt().toString() + " km dari lokasi kamu"
            } else {
                teksJarak.text = "Jarak ke lokasi tidak diketahui"
                kmdistance = 0F
            }

            auth = FirebaseAuth.getInstance()

            teksLihatMaps.setOnClickListener { openMap(address) }

            btnJanji.setOnClickListener {
                val intent = Intent(this, BuatJanji::class.java)
                intent.putExtra("Nama", namaKonselor.text.toString())
                intent.putExtra("Lokasi", textLokasi.text.toString())
                intent.putExtra("Id", id)
                intent.putExtra("Alamat", address)
                intent.putExtra("Jarak", kmdistance.roundToInt().toString())
                startActivity(intent)
            }

            btnKonsul.setOnClickListener {
                val intent = Intent(this, ChatKonsultasi::class.java)
                intent.putExtra("Nama", namaKonselor.text.toString())
                intent.putExtra("Id", id)
                startActivity(intent)
            }

            checkPromise(address, kmdistance.roundToInt().toString())
            checkChatKonsul()
            getDataHari(id)
            getName()
        } else {
            popAlertLocation("Apakah kamu mengizinkan Aplikasi Rehat untuk mengakses lokasimu?")
        }

    }

    // get route distance, must use Google Maps Distance API (Paid)
    fun getDistance(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): String? {
        var parsedDistance = ""
        var response: String
        val thread = Thread(Runnable {
            try {
                val url =
                    URL("http://maps.googleapis.com/maps/api/directions/json?origin=$lat1,$lon1&destination=$lat2,$lon2&sensor=false&units=metric&mode=driving")
                val conn: HttpURLConnection = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                val `in`: InputStream = BufferedInputStream(conn.inputStream)
                response = org.apache.commons.io.IOUtils.toString(`in`, "UTF-8")
                val jsonObject = JSONObject(response)
                val array = jsonObject.getJSONArray("routes")
                val routes = array.getJSONObject(0)
                val legs = routes.getJSONArray("legs")
                val steps = legs.getJSONObject(0)
                val distance = steps.getJSONObject("distance")
                parsedDistance = distance.getString("text")
            } catch (e: ProtocolException) {
                e.printStackTrace()
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        })
        thread.start()
        try {
            thread.join()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        return parsedDistance
    }
}
