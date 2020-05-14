package com.example.rehat.rvlisthari

import android.graphics.Color
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.rehat.R
import kotlinx.android.synthetic.main.list_hari.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

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
        val formatter = SimpleDateFormat("dd/MM/yyyy")
        var tgl = Date()
        val bulan = DateFormat.format("MM", tgl).toString()
        val hari = DateFormat.format("dd", tgl)
        holder.view.teksTanggal.text = "$hari ${toMonth(bulan.toInt())}"
        holder.view.constHari.setOnClickListener {
            if (list[position].value == 0) {
                list[position].value = 1
                holder.view.constHari.setBackgroundResource(R.drawable.rounded_button_green_borderless)
                holder.view.teksHari.setTextColor(Color.parseColor("#FFFFFF"))
                holder.view.teksTanggal.setTextColor(Color.parseColor("#FFFFFF"))
            } else {
                list[position].value = 0
                holder.view.constHari.setBackgroundColor(Color.parseColor("#F3F4FA"))
                holder.view.teksHari.setTextColor(Color.parseColor("#172B4D"))
                holder.view.teksTanggal.setTextColor(Color.parseColor("#172B4D"))
            }
        }
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

    private fun toMonth(value: Int): String {
        var bulan: String
        when (value) {
            1 -> bulan = "Jan"
            2 -> bulan = "Feb"
            3 -> bulan = "Mar"
            4 -> bulan = "Apr"
            5 -> bulan = "Mei"
            6 -> bulan = "Jun"
            7 -> bulan = "Jul"
            8 -> bulan = "Agu"
            9 -> bulan = "Sep"
            10 -> bulan = "Okt"
            11 -> bulan = "Nov"
            else -> bulan = "Des"
        }
        return bulan
    }
}