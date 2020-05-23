package com.example.rehat.fragmenthome

import android.app.Activity
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.example.rehat.R
import com.example.rehat.rvlistmateri.Adapter
import com.example.rehat.rvlistmateri.Materi
import com.example.rehat.rvlistsubmateri.AdapterSub
import com.example.rehat.rvlistsubmateri.SubMateri
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_edukasi.*
import kotlinx.android.synthetic.main.fragment_edukasi.view.*


/**
 * A simple [Fragment] subclass.
 */
class EdukasiFragment : Fragment() {

    private lateinit var ref: DatabaseReference
    private lateinit var root: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_edukasi, container, false)

        root.rvMateri.setHasFixedSize(true)
        root.rvMateri.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(activity)
        root.rvHasilSearch.setHasFixedSize(true)
        root.rvHasilSearch.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)

        getDataMateri(root)

        root.editTextSearch.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                getDataMateriSearch(root, root.editTextSearch.text.toString())
                hideKeyboard()
                return@setOnKeyListener true
            }
            false
        }

        return root
    }

    private fun getDataMateri(view: View) {
        val daftarMateri = arrayListOf<Materi>()
        ref = FirebaseDatabase.getInstance().getReference("Materi")
        ref.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()) {
                    var i = 1
                    for(h in p0.children) {
                        val judul = h.child("judul").value.toString()
                        val sub = h.child("jumlahsub").value.toString()
                        val background = h.child("background").value.toString()
                        val idmateri = h.child("materi_id").value.toString()
                        daftarMateri.add(Materi(idmateri,"Chapter $i: $judul", "$sub Materi", background.toInt()))
                        i += 1
                    }
                    val adapter = Adapter(daftarMateri)
                    adapter.notifyDataSetChanged()
                    view.rvMateri.adapter = adapter
                }
            }
        })
    }

    private fun getDataMateriSearch(view: View, parameter: String) {
        val daftarSubMateri = arrayListOf<SubMateri>()
        val daftarSubMateriFiltered = arrayListOf<SubMateri>()
        ref = FirebaseDatabase.getInstance().getReference("submateri")
        ref.orderByChild("judul").addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()) {
                    for (h in p0.children) {
                        val judul = h.child("judul").value.toString()
                        val gambar = h.child("src_img").value.toString()
                        val jenis = h.child("jenis").value.toString()
                        val isi = h.child("isi").value.toString()
                        val deskripsigambar = h.child("img_desc").value.toString()
                        val id = h.key.toString()
                        daftarSubMateri.add(SubMateri(judul, gambar, jenis, isi, getBackgroundColor(id.toInt()), deskripsigambar, id))
                    }
                    for (h in daftarSubMateri) {
                        if (h.judul.toLowerCase().contains(parameter.toLowerCase())) {
                            daftarSubMateriFiltered.add(h)
                        }
                    }
                    if (parameter == "") {
                        changeView(3)
                    } else {
                        when (daftarSubMateriFiltered.size) {
                            0 -> changeView(1)
                            else -> changeView(2)
                        }
                    }
                    val adapter = AdapterSub(daftarSubMateriFiltered)
                    adapter.notifyDataSetChanged()
                    val tekstotal = "Total ${daftarSubMateriFiltered.size.toString()}"
                    view.teksTotalEdukasi.text = tekstotal
                    view.rvHasilSearch.adapter = adapter
                }
            }
        })
    }

    private fun changeView(no: Int) {
        when (no) {
            1 -> changeToDataNol()
            2 -> changeToResult()
            else -> changeToDefault()
        }
    }

    private fun changeToDataNol() {
        root.edukasiDaftar.visibility = View.GONE
        root.edukasiHei.visibility = View.GONE
        root.rvMateri.visibility = View.GONE
        root.teksTotalEdukasi.visibility = View.GONE
        root.rvHasilSearch.visibility = View.GONE
        root.imgNoData.visibility = View.VISIBLE
        root.teksKosong.visibility = View.VISIBLE
    }

    private fun changeToResult() {
        val const = ConstraintSet()
        const.clone(edukasiConst)
        const.connect(R.id.teksCopyright, ConstraintSet.TOP, R.id.rvHasilSearch, ConstraintSet.BOTTOM, 42)
        const.applyTo(edukasiConst)
        root.edukasiDaftar.visibility = View.VISIBLE
        root.edukasiHei.visibility = View.VISIBLE
        root.rvMateri.visibility = View.GONE
        root.teksTotalEdukasi.visibility = View.VISIBLE
        root.rvHasilSearch.visibility = View.VISIBLE
        root.imgNoData.visibility = View.GONE
        root.teksKosong.visibility = View.GONE
    }

    private fun changeToDefault() {
        val const = ConstraintSet()
        const.clone(edukasiConst)
        const.connect(R.id.teksCopyright, ConstraintSet.TOP, R.id.rvMateri, ConstraintSet.BOTTOM, 22)
        const.applyTo(edukasiConst)
        root.edukasiDaftar.visibility = View.VISIBLE
        root.edukasiHei.visibility = View.VISIBLE
        root.rvMateri.visibility = View.VISIBLE
        root.teksTotalEdukasi.visibility = View.GONE
        root.rvHasilSearch.visibility = View.GONE
        root.imgNoData.visibility = View.GONE
        root.teksKosong.visibility = View.GONE
    }

    private fun getBackgroundColor(num: Int): String {
        when (num) {
            1 -> return resources.getString(0+R.color.colorBlue)
            2 -> return resources.getString(0+R.color.colorPrimary)
            3 -> return resources.getString(0+R.color.colorPurple)
            4 -> return resources.getString(0+R.color.colorLightGreen)
            else -> return resources.getString(0+R.color.colorPink)
        }
    }

    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
