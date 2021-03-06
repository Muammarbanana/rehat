package com.example.rehat.rvlistmateri

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.rehat.R
import com.example.rehat.SubMateri
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
        when(list[position].background) {
            1 -> holder.view.cardConstraint.setBackgroundResource(R.drawable.card_1)
            2 -> holder.view.cardConstraint.setBackgroundResource(R.drawable.card_2)
            3 -> holder.view.cardConstraint.setBackgroundResource(R.drawable.card_3)
            4 -> holder.view.cardConstraint.setBackgroundResource(R.drawable.card_4)
            else -> holder.view.cardConstraint.setBackgroundResource(R.drawable.card_5)
        }
        holder.view.cardConstraint.setOnClickListener{
            val intent = Intent(holder.view.context, SubMateri::class.java)
            intent.putExtra("Id_materi", list[position].materi_id)
            intent.putExtra("Background", list[position].background.toString())
            intent.putExtra("Judul", list[position].judul)
            holder.view.context.startActivity(intent)
        }
    }
}