package com.example.rehat.rvlisthari

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.rehat.R
import kotlinx.android.synthetic.main.list_hari.view.*

class Adapter(private val list:ArrayList<Hari>): androidx.recyclerview.widget.RecyclerView.Adapter<Adapter.Holder>() {

    class Holder(val view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Adapter.Holder {
        return Holder(
            LayoutInflater.from(
                parent.context
            ).inflate(R.layout.list_hari, parent, false)
        )
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.view.teksHari.text = toDay(list[position].hari.toInt())
        holder.view.teksTanggal.text = list[position].tanggal
    }

    private fun toDay(value: Int): String {
        var hari : String
        when (value) {
            1 -> hari = "Senin"
            2 -> hari = "Selasa"
            3 -> hari = "Rabu"
            4 -> hari = "Kamis"
            5 -> hari = "Jumat"
            6 -> hari = "Sabtu"
            else -> hari = "Minggu"
        }
        return hari
    }
}