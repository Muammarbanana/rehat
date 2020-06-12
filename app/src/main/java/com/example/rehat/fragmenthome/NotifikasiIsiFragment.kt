package com.example.rehat.fragmenthome

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.rehat.R
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.fragment_notifikasi_isi.view.*
import kotlinx.android.synthetic.main.list_header_notifikasi.view.*
import kotlinx.android.synthetic.main.list_notifikasi.view.*

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

        root.rvNotifikasi.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(root.context)
        root.rvNotifikasi.setHasFixedSize(true)
        adapter.add(HeaderNotifikasiItem())
        adapter.add(NotifikasiItem("Reza Rahmawan telah menerima permohonan janji pertemuan kamu.", "", "Beberapa abad yang lalu"))
        adapter.add(NotifikasiItem("Reza Rahmawan telah menerima permohonan janji pertemuan kamu.", "", "Beberapa abad yang lalu"))
        root.rvNotifikasi.adapter = adapter

        return root
    }

}

class NotifikasiItem(val text: String, val image: String, val waktu: String): Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.list_notifikasi
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.notifInfo.text = text
        viewHolder.itemView.notifWaktu.text = waktu
        if (image != "") {
            Picasso.get().load(image).into(viewHolder.itemView.notifImage)
        }
    }

}

class HeaderNotifikasiItem(): Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.list_header_notifikasi
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

    }

}
