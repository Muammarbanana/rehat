package com.example.rehat.rvlisthari

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.rehat.R
import kotlinx.android.synthetic.main.list_waktu.view.*


class AdapterWaktu(private val list:ArrayList<String>) : androidx.recyclerview.widget.RecyclerView.Adapter<AdapterWaktu.Holder>() {

    class Holder(val view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            LayoutInflater.from(
                parent.context
            ).inflate(R.layout.list_waktu, parent, false)
        )
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.view.teksJam.text = list[position] + ".00"
    }

}