package com.example.rehat.fragmenthome

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.room.Room
import com.example.rehat.R
import com.example.rehat.SharedViewModel
import com.example.rehat.roomdb.MateriEntity
import com.example.rehat.roomdb.RoomDB
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_halaman_tersimpan.*
import kotlinx.android.synthetic.main.fragment_halaman_tersimpan.view.*

/**
 * A simple [Fragment] subclass.
 */
class HalamanTersimpanFragment : Fragment() {

    private lateinit var viewModel: SharedViewModel
    private val materi: ArrayList<MateriEntity> = ArrayList()
    private var roomDB: RoomDB? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_halaman_tersimpan, container, false)

        viewModel = activity?.run {
            ViewModelProviders.of(this)[SharedViewModel::class.java]
        } ?: throw Exception("Invalid Activity")

        view.btnBelum.setOnClickListener {
            viewModel.selectedTab("go to tab 1")
        }

        roomDB = Room.databaseBuilder(view.context, RoomDB::class.java, "materiDB").allowMainThreadQueries().build()

        getAllData()

        return view
    }

    fun insertToDb(materi: MateriEntity){
        roomDB?.materiDao()?.insert(materi)
    }

    fun deleteDataRoom(materi: MateriEntity) {
        roomDB?.materiDao()?.delete(materi)
    }

    fun getAllData(){
            val materi = roomDB?.materiDao()?.getAll()
            if (materi != null) {
                for (h in materi) {
                    Log.d("pantat", h.isi)
                }
            } else {
                Log.d("pantat", "hah, kosong")
            }
    }
}
