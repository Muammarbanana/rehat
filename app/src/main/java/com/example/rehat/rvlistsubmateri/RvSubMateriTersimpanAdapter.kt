package com.example.rehat.rvlistsubmateri

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.FragmentActivity
import androidx.room.Room
import com.example.rehat.IsiMateri
import com.example.rehat.R
import com.example.rehat.fragmenthome.HalamanTersimpanFragment
import com.example.rehat.roomdb.MateriEntity
import com.example.rehat.roomdb.RoomDB
import com.example.rehat.viewmodel.SharedViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.list_sub_materi.view.*
import kotlinx.android.synthetic.main.pop_alert.view.*
import kotlinx.android.synthetic.main.toast_layout.view.*


class AdapterTersimpan(private val list:ArrayList<MateriEntity>,private val fragcont: FragmentActivity, private val viewModel: SharedViewModel) : androidx.recyclerview.widget.RecyclerView.Adapter<AdapterTersimpan.Holder>(){

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
        var roomDB = Room.databaseBuilder(holder.view.context, RoomDB::class.java, "materiDB").allowMainThreadQueries().build()
        holder.view.judulSub.text = list[position].judul
        holder.view.imgSimpan.contentDescription = "Hapus materi dari daftar simpan"
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
            intent.putExtra("Slider", 0)
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
            val iduser = FirebaseAuth.getInstance().uid.toString()
            popAlert(roomDB, list[position].id, position, iduser, holder.view, ortu)
        }
        changeIconSimpan(roomDB, list[position].id, holder)
    }

    private fun changeIconSimpan(roomDB: RoomDB, id: String, holder: Holder){
        val iduser  = FirebaseAuth.getInstance().uid.toString()
        val data = roomDB?.materiDao()?.getDatabyID(id, iduser)
        if (data != null) {
            holder.view.imgSimpan.setImageResource(R.drawable.ic_simpan_materi_dark)
        }
    }

    private fun deleteData(roomDB: RoomDB, id: String, position: Int, iduser: String) {
        roomDB?.materiDao()?.deleteDatabyID(id, iduser)
        list.removeAt(position)
        notifyItemRemoved(position)
        if (list.size == 0) {
            val tr = fragcont.supportFragmentManager.beginTransaction()
            tr.replace(R.id.savedPageConst, HalamanTersimpanFragment())
            tr.commit()
        }
        viewModel.selectedTab("savedpagedel")
    }

    private fun popAlert(roomDB: RoomDB, id: String, position: Int, iduser: String, view: View, parent: ViewGroup) {
        val dialog = AlertDialog.Builder(view.context).create()
        val inflater = LayoutInflater.from(
            parent.context
        )
        val dialogView = inflater.inflate(R.layout.pop_alert, null)
        dialog.setView(dialogView)
        dialog.setCancelable(true)
        dialogView.cardAlert.radius = 10F
        dialogView.alertText.text = "Apakah kamu yakin ingin menghapus materi dari daftar simpan?"
        dialogView.btnAccept.text = "Ya, Yakin"
        dialogView.btnAccept.setTextColor(Color.parseColor("#DB4437"))
        dialogView.btnCancel.setOnClickListener { dialog.dismiss() }
        dialogView.btnAccept.setOnClickListener {
            deleteData(roomDB, id, position, iduser)
            view.imgSimpan.setImageResource(R.drawable.ic_simpan_materi)
            val toastLayout = LayoutInflater.from(ortu.context).inflate(R.layout.toast_layout, ortu, false)
            val toast = Toast(view.context)
            toastLayout.textToast.text = "Materi dihapus dari daftar simpan"
            toast.duration = Toast.LENGTH_SHORT
            toast.view = toastLayout
            toast.show()
            dialog.dismiss()
        }
        dialog.show()
    }
}