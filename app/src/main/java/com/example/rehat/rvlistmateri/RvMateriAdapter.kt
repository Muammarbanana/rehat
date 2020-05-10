package com.example.rehat.rvlistmateri

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.rehat.R
import kotlinx.android.synthetic.main.list_materi.view.*


class Adapter(private val list:ArrayList<Materi>) : androidx.recyclerview.widget.RecyclerView.Adapter<Adapter.Holder>(){

    class Holder(val view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            LayoutInflater.from(
                parent.context
            ).inflate(R.layout.list_materi, parent, false)
        )
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.view.judulMateri.text = list[position].judul
        holder.view.subJudul.text = list[position].subJudul
        holder.view.cardMateri.setCardBackgroundColor(ContextCompat.getColor(holder.view.context,list[position].warna))
    }
}