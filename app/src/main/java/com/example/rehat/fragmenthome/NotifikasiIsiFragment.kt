package com.example.rehat.fragmenthome

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.rehat.R
import com.example.rehat.model.Notifikasi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
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

        root.rvNotifikasi.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(root.context)
        root.rvNotifikasi.adapter = adapter

        return root
    }

    private fun fetchNotification() {
        val iduser = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("notifikasi")
        ref.orderByChild("iduser").equalTo(iduser).addChildEventListener(object: ChildEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                adapter.clear()
                var date = ""
                val notifikasi = p0.getValue(Notifikasi::class.java)
                val idnotif = p0.key.toString()

                if (notifikasi != null) {
                    if (date != convertToDate(Date(notifikasi.timestamp))) {
                        adapter.add(HeaderNotifikasiItem(convertToDate(Date(notifikasi.timestamp))))
                    }
                    val teks = "${notifikasi.namakonselor} ${notifikasi.message}"
                    val selisih = System.currentTimeMillis() - notifikasi.timestamp
                    adapter.add(NotifikasiItem(teks, notifikasi.photo, hitungWaktu(selisih), notifikasi.statusbaca, idnotif))
                    date = convertToDate(Date(notifikasi.timestamp))
                }
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                adapter.clear()
                var date = ""
                val notifikasi = p0.getValue(Notifikasi::class.java)
                val idnotif = p0.key.toString()

                if (notifikasi != null) {
                    if (date != convertToDate(Date(notifikasi.timestamp))) {
                        adapter.add(HeaderNotifikasiItem(convertToDate(Date(notifikasi.timestamp))))
                    }
                    val teks = "${notifikasi.namakonselor} ${notifikasi.message}"
                    val selisih = System.currentTimeMillis() - notifikasi.timestamp
                    adapter.add(NotifikasiItem(teks, notifikasi.photo, hitungWaktu(selisih), notifikasi.statusbaca, idnotif))
                    date = convertToDate(Date(notifikasi.timestamp))
                }
            }

            override fun onChildRemoved(p0: DataSnapshot) {
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

class NotifikasiItem(val text: String, val image: String, val waktu: String, val statusbaca: Int, val idnotif: String): Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.list_notifikasi
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.notifInfo.text = text
        viewHolder.itemView.notifWaktu.text = waktu
        if (image != "") {
            Picasso.get().load(image).resize(56,56).into(viewHolder.itemView.notifImage)
        }
        if (statusbaca == 0) {
            viewHolder.itemView.constListNotifikasi.setBackgroundColor(Color.parseColor("#E8FFEB"))
        } else {
            viewHolder.itemView.constListNotifikasi.setBackgroundColor(Color.parseColor("#FFFFFF"))
        }
        viewHolder.itemView.constListNotifikasi.setOnClickListener {
            val ref = FirebaseDatabase.getInstance().getReference("notifikasi/$idnotif")
            ref.child("statusbaca").setValue(1)
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
