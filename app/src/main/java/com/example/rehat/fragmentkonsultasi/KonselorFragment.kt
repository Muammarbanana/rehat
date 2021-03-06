package com.example.rehat.fragmentkonsultasi

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.rehat.R
import com.example.rehat.rvlistkonselor.Adapter
import com.example.rehat.rvlistkonselor.Konselor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_konselor.view.*
import java.util.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class KonselorFragment : Fragment() {

    private lateinit var ref: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_konselor, container, false)

        view.rvKonselor.setHasFixedSize(true)
        view.rvKonselor.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(activity)

        auth = FirebaseAuth.getInstance()

        getName(view)
        getDataKonselor(view)

        return view
    }

    private fun getDataKonselor(view: View) {
        val daftarKonselor = arrayListOf<Konselor>()
        ref = FirebaseDatabase.getInstance().getReference("konselor")
        ref.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                daftarKonselor.clear()
                if(p0.exists()) {
                    for(h in p0.children) {
                        val nama = h.child("nama_konselor").value.toString()
                        val tempat = h.child("tempat").value.toString()
                        val spesialis = h.child("spesialis").value.toString()
                        val urlfoto = h.child("url_foto").value.toString()
                        val bio = h.child("bio").value.toString()
                        val id = h.key.toString()
                        val alamat = h.child("alamat").value.toString()
                        daftarKonselor.add(Konselor(id, nama, spesialis, tempat, urlfoto, bio, alamat))
                    }
                    val adapter = Adapter(daftarKonselor)
                    adapter.notifyDataSetChanged()
                    view.rvKonselor.adapter = adapter
                }
            }
        })
    }

    private fun getName(view: View) {
        ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.orderByKey().equalTo(auth.currentUser?.uid!!).addValueEventListener(object:
            ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    for (h in p0.children) {
                        val name = h.child("nama").value.toString()
                        view.konselorHei.text = "Hai $name,"
                    }
                }
            }
        })
    }
}
