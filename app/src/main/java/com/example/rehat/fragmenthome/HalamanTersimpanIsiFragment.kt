package com.example.rehat.fragmenthome

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.rehat.R
import com.example.rehat.roomdb.MateriEntity
import com.example.rehat.roomdb.RoomDB
import com.example.rehat.rvlistsubmateri.AdapterTersimpan
import kotlinx.android.synthetic.main.fragment_edukasi.view.*
import kotlinx.android.synthetic.main.fragment_halaman_tersimpan_isi.*
import kotlinx.android.synthetic.main.fragment_halaman_tersimpan_isi.view.*
import kotlinx.android.synthetic.main.fragment_halaman_tersimpan_isi.view.rvMateriTersimpan

/**
 * A simple [Fragment] subclass.
 */
class HalamanTersimpanIsiFragment : Fragment() {

    private var roomDB: RoomDB? = null
    private lateinit var root: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root =  inflater.inflate(R.layout.fragment_halaman_tersimpan_isi, container, false)

        root.rvMateriTersimpan.setHasFixedSize(true)
        root.rvMateriTersimpan.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(root.context)

        return root
    }

    override fun onResume() {
        super.onResume()
        roomDB = Room.databaseBuilder(root.context, RoomDB::class.java, "materiDB").allowMainThreadQueries().build()
        getAllData()
    }

    fun getAllData(){
        var listmateri = arrayListOf<MateriEntity>()
        val materi = roomDB?.materiDao()?.getAll()
        if (materi != null) {
            for (h in materi) {
                listmateri.add(h)
            }
            val adapter = AdapterTersimpan(listmateri)
            adapter.notifyDataSetChanged()
            root.rvMateriTersimpan.adapter = adapter
            root.teksTotal.text = "Total ${listmateri.size.toString()}"
        } else {
            Log.d("pantat", "hah, kosong")
        }
    }

}
