package com.example.rehat.fragmentkonsultasi

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.rehat.R
import com.example.rehat.RincianPersetujuan
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_persetujuan_isi.*
import kotlinx.android.synthetic.main.fragment_persetujuan_isi.view.*

/**
 * A simple [Fragment] subclass.
 */
class PersetujuanIsiFragment : Fragment() {

    private lateinit var ref: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var root: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_persetujuan_isi, container, false)

        auth = FirebaseAuth.getInstance()
        getDataJanji()
        getName()

        return root
    }

    private fun getDataJanji() {
        ref = FirebaseDatabase.getInstance().getReference("janji")
        ref.orderByChild("id_user").equalTo(auth.currentUser?.uid!!).addListenerForSingleValueEvent(object:
            ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    for (h in p0.children) {
                        val namadokter = h.child("namadokter").value.toString()
                        val alamat = h.child("tempat").value.toString() + ", " + h.child("alamat").value.toString()
                        val tanggal = h.child("tanggal").value.toString()
                        val jam = h.child("jam").value.toString()
                        val catatan = h.child("catatan").value.toString()
                        val status = h.child("status").value.toString()
                        val idjanji = h.key.toString()
                        root.agrName.text = namadokter
                        root.agrAddress.text = alamat
                        root.agrTanggal.text = tanggal
                        root.agrJam.text = jam + " WIB"
                        root.agrCatatan.text = catatan
                        root.agrAddress.contentDescription = "Alamat konsultasi, $alamat"
                        root.agrTanggal.contentDescription = "Hari dan tanggal, $tanggal"
                        root.agrJam.contentDescription = "Pukul, $jam WIB"
                        root.agrCatatan.contentDescription = "Catatan tambahan, $catatan"
                        when (status.toInt()) {
                            1 -> {
                                agrBtnStatus.text = "Diterima"
                                agrBtnStatus.setBackgroundResource(R.drawable.rounded_button_bot_green)
                                agrBtnStatus.contentDescription = "Status persetujuan diterima, lihat rincian pertemuan"
                                agrBtnStatus.setOnClickListener {
                                    val intent = Intent(root.context, RincianPersetujuan::class.java)
                                    intent.putExtra("idJanji", idjanji)
                                    root.context.startActivity(intent)
                                }
                            }
                            2 -> {
                                agrBtnStatus.text = "Ditolak"
                                agrBtnStatus.setBackgroundResource(R.drawable.rounded_button_bot_red)
                                agrBtnStatus.contentDescription = "Status persetujuan ditolak, lihat alasan penolakan"
                                agrBtnStatus.setOnClickListener{
                                    val intent = Intent(root.context, RincianPersetujuan::class.java)
                                    intent.putExtra("idJanji", idjanji)
                                    root.context.startActivity(intent)
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
        ref.orderByKey().equalTo(auth.currentUser?.uid!!).addValueEventListener(object:
            ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    for (h in p0.children) {
                        val name = h.child("nama").value.toString()
                        root.agrHei.text = "Hai $name,"
                    }
                }
            }
        })
    }

}
