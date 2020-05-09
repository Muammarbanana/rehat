package com.example.rehat.rvlistkonselor

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.rehat.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.list_konselor.view.*

class Adapter(private val list:ArrayList<Konselor>) : RecyclerView.Adapter<Adapter.Holder>(){

    class Holder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            LayoutInflater.from(
                parent.context
            ).inflate(R.layout.list_konselor, parent, false)
        )
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.view.namaKonselor.text = list[position].nama
        holder.view.profesi.text = list[position].profesi
        holder.view.lokasiKonselor.text = list[position].lokasi
        Picasso.get().load(list[position].urlfoto).resize(300,375).into(holder.view.fotoKonselor)
    }
}