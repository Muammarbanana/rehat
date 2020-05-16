package com.example.rehat.fragmentkonsultasi

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.rehat.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_persetujuan_isi.*

/**
 * A simple [Fragment] subclass.
 */
class PersetujuanIsiFragment : Fragment() {

    private lateinit var ref: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_persetujuan_isi, container, false)

        auth = FirebaseAuth.getInstance()
        getDataJanji()

        return view
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
                        agrName.text = namadokter
                        agrAddress.text = alamat
                        agrTanggal.text = tanggal
                        agrJam.text = jam + " WIB"
                        agrCatatan.text = catatan
                    }
                }
            }
        })
    }

}
