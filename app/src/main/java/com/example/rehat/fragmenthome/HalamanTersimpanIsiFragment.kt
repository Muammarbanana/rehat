package com.example.rehat.fragmenthome

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.room.Room
import com.example.rehat.R
import com.example.rehat.viewmodel.SharedViewModel
import com.example.rehat.viewmodel.ViewModelFactory
import com.example.rehat.roomdb.MateriEntity
import com.example.rehat.roomdb.RoomDB
import com.example.rehat.rvlistsubmateri.AdapterTersimpan
import kotlinx.android.synthetic.main.fragment_halaman_tersimpan_isi.view.*
import kotlinx.android.synthetic.main.fragment_halaman_tersimpan_isi.view.rvMateriTersimpan

/**
 * A simple [Fragment] subclass.
 */
class HalamanTersimpanIsiFragment : Fragment() {

    private var roomDB: RoomDB? = null
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

        viewModel = ViewModelProviders.of(this,
            ViewModelFactory(roomDB!!.materiDao())
        )[SharedViewModel::class.java]

        viewModel.listenMateri().observeForever {
            getAllData()
        }

        return root
    }

    fun getAllData(){
        var listmateri = arrayListOf<MateriEntity>()
        roomDB?.materiDao()?.getAll()?.observe(this,  Observer{
            if (it != null) {
                for (h in it) {
                    listmateri.add(h)
                }
                val adapter = AdapterTersimpan(listmateri)
                adapter.notifyDataSetChanged()
                root.rvMateriTersimpan.adapter = adapter
                root.teksTotal.text = "Total: ${listmateri.size.toString()}"
            }
        })
    }
}
