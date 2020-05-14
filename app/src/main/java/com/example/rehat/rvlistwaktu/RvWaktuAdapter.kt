package com.example.rehat.rvlistwaktu

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.rehat.R
import kotlinx.android.synthetic.main.list_waktu.view.*


class AdapterWaktu(private var list:ArrayList<Waktu>) : androidx.recyclerview.widget.RecyclerView.Adapter<AdapterWaktu.Holder>() {

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
        holder.view.teksJam.text = list[position].jam + ".00"
        holder.view.constWaktu.setOnClickListener{
            if (list[position].value == 0) {
                list[position].value = 1
                holder.view.constWaktu.setBackgroundResource(R.drawable.rounded_button_green_borderless)
                holder.view.teksJam.setTextColor(Color.parseColor("#FFFFFF"))
            } else {
                list[position].value = 0
                holder.view.constWaktu.setBackgroundColor(Color.parseColor("#F3F4FA"))
                holder.view.teksJam.setTextColor(Color.parseColor("#172B4D"))
            }
        }
    }

}