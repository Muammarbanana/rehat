package com.example.rehat.rvlistkonselor

import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivities
import androidx.core.content.ContextCompat.startActivity
import com.example.rehat.ProfilKonselor
import com.example.rehat.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.list_konselor.view.*

class Adapter(private val list:ArrayList<Konselor>) : androidx.recyclerview.widget.RecyclerView.Adapter<Adapter.Holder>(){

    class Holder(val view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view)

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
        holder.view.btnLihatProfil.setOnClickListener {
            val intent = Intent(holder.view.context, ProfilKonselor::class.java)
            intent.putExtra("Id", list[position].id)
            intent.putExtra("Nama", list[position].nama)
            intent.putExtra("Spesialisasi", list[position].profesi)
            intent.putExtra("Lokasi", list[position].lokasi)
            intent.putExtra("Foto", list[position].urlfoto)
            intent.putExtra("Bio", list[position].bio)
            intent.putExtra("Alamat", list[position].alamat)
            holder.view.context.startActivity(intent)
        }
    }
}