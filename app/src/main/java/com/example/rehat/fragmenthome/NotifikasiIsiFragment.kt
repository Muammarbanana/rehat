package com.example.rehat.fragmenthome

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintSet
import com.example.rehat.Home
import com.example.rehat.IsiMateri

import com.example.rehat.R
import com.example.rehat.model.Notifikasi
import com.example.rehat.model.NotifikasiMateri
import com.example.rehat.model.SubMateriKomplit
import com.example.rehat.rvlistsubmateri.SubMateri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.Group
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.fragment_notifikasi_isi.view.*
import kotlinx.android.synthetic.main.list_header_notifikasi.view.*
import kotlinx.android.synthetic.main.list_notifikasi.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class NotifikasiIsiFragment : Fragment() {

    private lateinit var root: View
    val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root =  inflater.inflate(R.layout.fragment_notifikasi_isi, container, false)

        fetchNotification()

        val layoutManager = androidx.recyclerview.widget.LinearLayoutManager(root.context)
        root.rvNotifikasi.layoutManager = layoutManager
        root.rvNotifikasi.adapter = adapter

        return root
    }

    private fun fetchNotification() {
        var listNotif = mutableListOf<NotifikasiItem>()
        val iduser = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("notifikasi")
        ref.orderByChild("iduser").equalTo(iduser).addValueEventListener(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                var date = ""
                if (p0.exists()) {
                    for (h in p0.children) {
                        if (h.child("jenis").value.toString() != "null") {
                            val notifikasimateri = h.getValue(NotifikasiMateri::class.java)
                            if (notifikasimateri != null) {
                                val idnotif = h.key.toString()
                                val teks = "${notifikasimateri.message} ${notifikasimateri.judul}"
                                val selisih = System.currentTimeMillis() - notifikasimateri.timestamp
                                val submateri = h.child("submateri").getValue(SubMateriKomplit::class.java)
                                listNotif.add(NotifikasiItem(teks, notifikasimateri.jenis.toString(), hitungWaktu(selisih), notifikasimateri.statusbaca, idnotif, notifikasimateri.timestamp, submateri!!))
                            }
                        } else {
                            val notifikasi = h.getValue(Notifikasi::class.java)
                            if (notifikasi != null) {
                                val idnotif = h.key.toString()
                                val teks = "${notifikasi.namakonselor} ${notifikasi.message}"
                                val selisih = System.currentTimeMillis() - notifikasi.timestamp
                                val submateri = SubMateriKomplit("", "", 0, "", 0, "", 0)
                                listNotif.add(NotifikasiItem(teks, notifikasi.photo, hitungWaktu(selisih), notifikasi.statusbaca, idnotif, notifikasi.timestamp, submateri))
                            }
                        }
                    }
                    date = ""
                    listNotif.reverse()
                    var listNotifwithHead = mutableListOf<Group>()
                    for (i in 0 until listNotif.size) {
                        if (date != convertToDate(Date(listNotif[i].timestamp))) {
                            listNotifwithHead.add(HeaderNotifikasiItem(convertToDate(Date(listNotif[i].timestamp))))
                        }
                        listNotifwithHead.add(listNotif[i])
                        date = convertToDate(Date(listNotif[i].timestamp))
                    }
                    adapter.addAll(listNotifwithHead)
                }
            }

        })
    }

    private fun convertToDate(timestamp: Date): String {
        var datevalue: String
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val sdf2 = SimpleDateFormat("dd", Locale.getDefault())
        val currentdate = Calendar.getInstance().time
        if (sdf.format(currentdate) == sdf.format(timestamp)) {
            datevalue = "Hari Ini"
        } else if (sdf2.format(timestamp).toInt() == sdf2.format(currentdate).toInt() - 1) {
            datevalue = "Kemarin"
        } else {
            datevalue = sdf.format(timestamp)
        }
        return datevalue
    }

    private fun hitungWaktu(selisih: Long): String {
        val hari = selisih/86400000
        val jam = selisih/3600000
        val menit = selisih/60000
        val detik = selisih/1000
        var hasil: String
        if (selisih > 86400000) {
            hasil = "$hari hari yang lalu"
        } else if (selisih > 3600000) {
            hasil = "$jam jam yang lalu"
        } else if (selisih > 60000) {
            hasil = "$menit menit yang lalu"
        } else {
            hasil = "$detik detik yang lalu"
        }
        return hasil
    }
}

class NotifikasiItem(val text: String, val image: String, val waktu: String, val statusbaca: Int, val idnotif: String, val timestamp: Long, val submateri: SubMateriKomplit): Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.list_notifikasi
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.notifInfo.text = text
        viewHolder.itemView.notifWaktu.text = waktu
        if (image.length == 1) {
            if (image.toInt() == 1) {
                viewHolder.itemView.notifImage.visibility = View.GONE
                viewHolder.itemView.notifImageMateri.visibility = View.VISIBLE
                val const = ConstraintSet()
                const.clone(viewHolder.itemView.constListNotifikasi)
                const.connect(R.id.notifInfo, ConstraintSet.START, R.id.notifImageMateri, ConstraintSet.END, 12)
                const.applyTo(viewHolder.itemView.constListNotifikasi)
                viewHolder.itemView.notifImageMateri.setImageResource(R.drawable.ic_reading_1)
            } else {
                viewHolder.itemView.notifImage.visibility = View.GONE
                viewHolder.itemView.notifImageMateri.visibility = View.VISIBLE
                val const = ConstraintSet()
                const.clone(viewHolder.itemView.constListNotifikasi)
                const.connect(R.id.notifInfo, ConstraintSet.START, R.id.notifImageMateri, ConstraintSet.END, 12)
                const.applyTo(viewHolder.itemView.constListNotifikasi)
                viewHolder.itemView.notifImageMateri.setImageResource(R.drawable.ic_notes)
            }
            val refmateri = FirebaseDatabase.getInstance().getReference("submateri")
            val daftarSub = arrayListOf<SubMateri>()
            refmateri.orderByChild("materi_id").equalTo(submateri.materi_id.toDouble()).addListenerForSingleValueEvent(object: ValueEventListener{
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
                            val idsub = h.key.toString()
                            daftarSub.add(SubMateri(judul, gambar, jenis, isi, getBackgroundColor(viewHolder, submateri.materi_id), deskripsigambar, idsub, 1))
                        }
                        viewHolder.itemView.constListNotifikasi.setOnClickListener {
                            val ref = FirebaseDatabase.getInstance().getReference("notifikasi/$idnotif")
                            ref.child("statusbaca").setValue(1)
                            val pos = submateri.urutan-1
                            val intent = Intent(viewHolder.itemView.context, IsiMateri::class.java)
                            var daftarJudul = arrayListOf<String>()
                            var daftarGambar = arrayListOf<String>()
                            var daftarIsi = arrayListOf<String>()
                            var daftarDesk = arrayListOf<String>()
                            intent.putExtra("Warna", daftarSub[pos].color)
                            intent.putExtra("Position", pos.toString())
                            intent.putExtra("Slider", daftarSub[pos].slider)
                            for (h in daftarSub) {
                                daftarJudul.add(h.judul)
                                daftarGambar.add(h.gambar)
                                daftarIsi.add(h.isi)
                                daftarDesk.add(h.desc)
                            }
                            intent.putExtra("DaftarJudul", daftarJudul)
                            intent.putExtra("DaftarGambar", daftarGambar)
                            intent.putExtra("DaftarIsi", daftarIsi)
                            intent.putExtra("DaftarDesk", daftarDesk)
                            viewHolder.itemView.context.startActivity(intent)
                        }
                    }
                }
            })
        } else {
            if (image != "") {
                Picasso.get().load(image).resize(56,56).into(viewHolder.itemView.notifImage)
                viewHolder.itemView.constListNotifikasi.setOnClickListener {
                    val ref = FirebaseDatabase.getInstance().getReference("notifikasi/$idnotif")
                    ref.child("statusbaca").setValue(1)
                    var intent = Intent(viewHolder.itemView.context, Home::class.java)
                    intent.putExtra("DataTabChat", "9")
                    intent = intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent = intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    viewHolder.itemView.context.startActivity(intent)
                    (viewHolder.itemView.context as Activity).finish()
                }
            }
        }
        if (statusbaca == 0) {
            viewHolder.itemView.constListNotifikasi.setBackgroundColor(Color.parseColor("#E8FFEB"))
        } else {
            viewHolder.itemView.constListNotifikasi.setBackgroundColor(Color.parseColor("#FFFFFF"))
        }
    }

    private fun getBackgroundColor(viewHolder: GroupieViewHolder, num: Int): String {
        when (num) {
            1 -> return viewHolder.itemView.resources.getString(0+R.color.colorBlue)
            2 -> return viewHolder.itemView.resources.getString(0+R.color.colorPrimary)
            3 -> return viewHolder.itemView.resources.getString(0+R.color.colorPurple)
            4 -> return viewHolder.itemView.resources.getString(0+R.color.colorLightGreen)
            else -> return viewHolder.itemView.resources.getString(0+R.color.colorPink)
        }
    }
}

class HeaderNotifikasiItem(val text: String): Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.list_header_notifikasi
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.teksHeader.text = text
    }

}
