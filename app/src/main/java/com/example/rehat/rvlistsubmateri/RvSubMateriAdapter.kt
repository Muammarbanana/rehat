package com.example.rehat.rvlistsubmateri

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.example.rehat.IsiMateri
import com.example.rehat.R
import com.example.rehat.fragmenthome.HalamanTersimpanIsiFragment
import com.example.rehat.roomdb.MateriEntity
import com.example.rehat.roomdb.RoomDB
import kotlinx.android.synthetic.main.list_sub_materi.view.*
import kotlinx.android.synthetic.main.toast_layout.view.*


class AdapterSub(private val list:ArrayList<SubMateri>, private val fragcont: FragmentActivity, private val fragindicator: Int) : androidx.recyclerview.widget.RecyclerView.Adapter<AdapterSub.Holder>() {

    class Holder(val view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view)

    private lateinit var ortu: ViewGroup

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        this.ortu = parent
        return Holder(
            LayoutInflater.from(
                parent.context
            ).inflate(R.layout.list_sub_materi, parent, false)
        )
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val roomDB = Room.databaseBuilder(holder.view.context, RoomDB::class.java, "materiDB").allowMainThreadQueries().build()
        val jumlahdata = roomDB.materiDao().getDataCount()
        holder.view.judulSub.text = list[position].judul
        when (list[position].jenis.toInt()) {
            1 -> holder.view.imgMateri.setImageResource(R.drawable.ic_img_reading)
            2 -> holder.view.imgMateri.setImageResource(R.drawable.ic_img_brain)
            3 -> holder.view.imgMateri.setImageResource(R.drawable.ic_img_thinking)
            else -> holder.view.imgMateri.setImageResource(R.drawable.ic_img_management)
        }
        holder.view.cardConstSubMateri.setOnClickListener {
            val intent = Intent(holder.view.context, IsiMateri::class.java)
            var daftarJudul = arrayListOf<String>()
            var daftarGambar = arrayListOf<String>()
            var daftarIsi = arrayListOf<String>()
            var daftarDesk = arrayListOf<String>()
            intent.putExtra("Warna", list[position].color)
            intent.putExtra("Position", position.toString())
            intent.putExtra("Slider", list[position].slider)
            for (h in list) {
                daftarJudul.add(h.judul)
                daftarGambar.add(h.gambar)
                daftarIsi.add(h.isi)
                daftarDesk.add(h.desc)
            }
            intent.putExtra("DaftarJudul", daftarJudul)
            intent.putExtra("DaftarGambar", daftarGambar)
            intent.putExtra("DaftarIsi", daftarIsi)
            intent.putExtra("DaftarDesk", daftarDesk)
            holder.view.context.startActivity(intent)
        }
        holder.view.imgSimpan.setOnClickListener {
            if (fragindicator == 0) {
                if (jumlahdata == 0) {
                    val tr = fragcont.supportFragmentManager.beginTransaction()
                    tr.replace(R.id.empSavedPageConst, HalamanTersimpanIsiFragment())
                    tr.commit()
                } else {
                    val tr = fragcont.supportFragmentManager.beginTransaction()
                    tr.replace(R.id.savedPageConst, HalamanTersimpanIsiFragment())
                    tr.commit()
                }
            }
            if (holder.view.imgSimpan.tag == R.drawable.ic_simpan_materi_dark) {
                val toastLayout = LayoutInflater.from(ortu.context).inflate(R.layout.toast_layout, ortu, false)
                val toast = Toast(holder.view.context)
                toastLayout.textToast.text = "Materi sudah ada di dalam daftar simpan"
                toast.duration = Toast.LENGTH_SHORT
                toast.view = toastLayout
                toast.show()
            } else {
                insertToDb(
                    MateriEntity(
                        list[position].judul,
                        list[position].id,
                        list[position].jenis,
                        list[position].gambar,
                        list[position].isi,
                        list[position].color,
                        list[position].desc
                ), roomDB)
                holder.view.imgSimpan.setImageResource(R.drawable.ic_simpan_materi_dark)
                holder.view.imgSimpan.tag = R.drawable.ic_simpan_materi_dark
                val toastLayout = LayoutInflater.from(ortu.context).inflate(R.layout.toast_layout, ortu, false)
                val toast = Toast(holder.view.context)
                toastLayout.textToast.text = "Materi berhasil disimpan"
                toast.duration = Toast.LENGTH_SHORT
                toast.view = toastLayout
                toast.show()
            }
        }
        changeIconSimpan(roomDB, list[position].id, holder)
    }

    private fun insertToDb(materi: MateriEntity, roomDB: RoomDB){
        roomDB?.materiDao()?.insert(materi)
    }

    private fun changeIconSimpan(roomDB: RoomDB, id: String, holder: Holder){
        val data = roomDB?.materiDao()?.getDatabyID(id)
        if (data != null) {
            holder.view.imgSimpan.setImageResource(R.drawable.ic_simpan_materi_dark)
            holder.view.imgSimpan.tag = R.drawable.ic_simpan_materi_dark
            holder.view.imgSimpan.contentDescription = "Hapus materi dari daftar simpan"
        }
    }
}