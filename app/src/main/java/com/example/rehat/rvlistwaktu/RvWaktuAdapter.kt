package com.example.rehat.rvlistwaktu

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.rehat.R
import kotlinx.android.synthetic.main.list_waktu.view.*


class AdapterWaktu(private var list:ArrayList<Waktu>) : androidx.recyclerview.widget.RecyclerView.Adapter<AdapterWaktu.Holder>() {

    private var selectedItemPosition = -1
    
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
        if (list[position].value == 2) {
            holder.view.constWaktu.isClickable = false
            holder.view.teksJam.text = list[position].jam + ".00"
            holder.view.constWaktu.setBackgroundResource(R.drawable.rounded_button_gray_borderless)
            holder.view.teksJam.setTextColor(Color.parseColor("#FFFAFA"))
            holder.view.materialCardWaktu.strokeColor = Color.parseColor("#B5B3BF")
        } else {
            holder.view.teksJam.text = list[position].jam + ".00"
            holder.view.constWaktu.setOnClickListener{
                changeValueSelected(holder)
            }
            if (position == selectedItemPosition) {
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

    private fun changeValueSelected(holder: Holder) {
        selectedItemPosition = holder.adapterPosition
        notifyDataSetChanged()
    }
}