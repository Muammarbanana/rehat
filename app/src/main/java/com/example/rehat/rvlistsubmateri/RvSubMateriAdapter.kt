package com.example.rehat.rvlistsubmateri

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.rehat.R
import kotlinx.android.synthetic.main.list_materi.view.*
import kotlinx.android.synthetic.main.list_sub_materi.view.*


class Adapter(private val list:ArrayList<SubMateri>) : androidx.recyclerview.widget.RecyclerView.Adapter<Adapter.Holder>(){

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
        holder.view.judulSub.text = list[position].judul
    }
}