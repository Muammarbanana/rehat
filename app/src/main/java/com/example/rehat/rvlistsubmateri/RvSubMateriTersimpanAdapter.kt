package com.example.rehat.rvlistsubmateri

import android.content.Intent
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.room.Room
import com.example.rehat.IsiMateri
import com.example.rehat.R
import com.example.rehat.roomdb.MateriEntity
import com.example.rehat.roomdb.RoomDB
import kotlinx.android.synthetic.main.list_sub_materi.view.*


class AdapterTersimpan(private val list:ArrayList<MateriEntity>) : androidx.recyclerview.widget.RecyclerView.Adapter<AdapterTersimpan.Holder>(){

    class Holder(val view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            LayoutInflater.from(
                parent.context
            ).inflate(R.layout.list_sub_materi, parent, false)
        )
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        var roomDB = Room.databaseBuilder(holder.view.context, RoomDB::class.java, "materiDB").allowMainThreadQueries().build()
        holder.view.judulSub.text = list[position].judul
        when (list[position].jenis.toInt()) {
            1 -> holder.view.imgMateri.setImageResource(R.drawable.ic_img_reading)
            2 -> holder.view.imgMateri.setImageResource(R.drawable.ic_img_brain)
            3 -> holder.view.imgMateri.setImageResource(R.drawable.ic_img_thinking)
            else -> holder.view.imgMateri.setImageResource(R.drawable.ic_img_management)
        }
        holder.view.cardConstSubMateri.setOnClickListener {
            val intent = Intent(holder.view.context, IsiMateri::class.java)
            intent.putExtra("Judul", list[position].judul)
            intent.putExtra("Gambar", list[position].gambar)
            intent.putExtra("Isi", list[position].isi)
            intent.putExtra("Warna", list[position].color)
            intent.putExtra("Desk", list[position].desc)
            holder.view.context.startActivity(intent)
        }
        holder.view.imgSimpan.setOnClickListener {
            deleteData(roomDB, list[position].id)
            holder.view.imgSimpan.setImageResource(R.drawable.ic_simpan_materi)
            Toast.makeText(holder.view.context, "Materi dihapus dari daftar simpan", Toast.LENGTH_SHORT).show()
        }
        changeIconSimpan(roomDB, list[position].id, holder)
    }

    private fun changeIconSimpan(roomDB: RoomDB, id: String, holder: Holder){
        val data = roomDB?.materiDao()?.getDatabyID(id)
        if (data != null) {
            holder.view.imgSimpan.setImageResource(R.drawable.ic_simpan_materi_dark)
        }
    }

    private fun deleteData(roomDB: RoomDB, id: String) {
        roomDB?.materiDao()?.deleteDatabyID(id)
    }
}