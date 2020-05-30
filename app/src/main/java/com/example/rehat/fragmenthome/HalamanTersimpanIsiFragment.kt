package com.example.rehat.fragmenthome

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.room.Room
import com.example.rehat.R
import com.example.rehat.viewmodel.SharedViewModel
import com.example.rehat.viewmodel.ViewModelFactory
import com.example.rehat.roomdb.MateriEntity
import com.example.rehat.roomdb.RoomDB
import com.example.rehat.rvlistsubmateri.AdapterTersimpan
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_halaman_tersimpan_isi.view.*
import kotlinx.android.synthetic.main.fragment_halaman_tersimpan_isi.view.rvMateriTersimpan

/**
 * A simple [Fragment] subclass.
 */
class HalamanTersimpanIsiFragment : Fragment() {

    private var roomDB: RoomDB? = null
    private lateinit var ref: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var root: View
    private lateinit var viewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root =  inflater.inflate(R.layout.fragment_halaman_tersimpan_isi, container, false)

        roomDB = Room.databaseBuilder(root.context, RoomDB::class.java, "materiDB").allowMainThreadQueries().build()

        root.rvMateriTersimpan.setHasFixedSize(true)
        root.rvMateriTersimpan.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(root.context)

        ref = FirebaseDatabase.getInstance().getReference("Users")
        auth = FirebaseAuth.getInstance()

        viewModel = ViewModelProviders.of(this,
            ViewModelFactory(roomDB!!.materiDao())
        )[SharedViewModel::class.java]

        viewModel.listenMateri().observeForever {
            getAllData()
        }

        // Update Total text when one of the item deleted
        viewModel.selected.observeForever(androidx.lifecycle.Observer{
            if (it == "savedpagedel") {
                getAllData()
            }
        })

        getName()

        return root
    }

    override fun onResume() {
        super.onResume()
        getAllData()
    }

    fun getAllData(){
        var listmateri = arrayListOf<MateriEntity>()
        roomDB?.materiDao()?.getAll()?.observe(this,  Observer{
            if (it != null) {
                for (h in it) {
                    listmateri.add(h)
                }
                if (listmateri.size == 0) {
                    val tr = fragmentManager?.beginTransaction()
                    tr?.replace(R.id.savedPageConst, HalamanTersimpanFragment())
                    tr?.commit()
                } else {
                    val adapter = AdapterTersimpan(listmateri, this@HalamanTersimpanIsiFragment.activity!!, viewModel)
                    adapter.notifyDataSetChanged()
                    root.rvMateriTersimpan.adapter = adapter
                    root.teksTotal.text = "Total: ${adapter.itemCount}"
                }
            }
        })
    }

    private fun getName() {
        ref.orderByKey().equalTo(auth.currentUser?.uid!!).addValueEventListener(object:
            ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    for (h in p0.children) {
                        val name = h.child("nama").value.toString()
                        root.savedHei.text = "Hai $name,"
                    }
                }
            }
        })
    }
}
