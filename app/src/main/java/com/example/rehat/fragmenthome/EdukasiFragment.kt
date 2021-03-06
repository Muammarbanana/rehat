package com.example.rehat.fragmenthome

import android.app.Activity
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.rehat.R
import com.example.rehat.rvlistmateri.Adapter
import com.example.rehat.rvlistmateri.Materi
import com.example.rehat.rvlistsubmateri.AdapterSub
import com.example.rehat.rvlistsubmateri.SubMateri
import com.example.rehat.viewmodel.SharedViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.fragment_edukasi.*
import kotlinx.android.synthetic.main.fragment_edukasi.view.*


/**
 * A simple [Fragment] subclass.
 */
class EdukasiFragment : Fragment() {

    private lateinit var ref: DatabaseReference
    private lateinit var root: View
    private lateinit var viewModel: SharedViewModel
    private lateinit var auth: FirebaseAuth
    private val SPEECH_REQUEST_CODE = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_edukasi, container, false)

        viewModel = activity?.run {
            ViewModelProviders.of(this)[SharedViewModel::class.java]
        } ?: throw Exception("Invalid Activity")

        auth = FirebaseAuth.getInstance()

        root.rvMateri.setHasFixedSize(true)
        root.rvMateri.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(activity)
        root.rvHasilSearch.setHasFixedSize(true)
        root.rvHasilSearch.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)

        getName()
        getDataMateri(root)

        /**root.editTextSearch.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                getDataMateriSearch(root, root.editTextSearch.text.toString(), viewModel)
                hideKeyboard()
                return@setOnKeyListener true
            }
            false
        }**/

        root.editTextSearch.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                getDataMateriSearch(root, s.toString(), viewModel)
            }

        })

        root.btnSearchMic.setOnClickListener {
            getVoice()
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

    private fun getDataMateriSearch(view: View, parameter: String, viewModel: SharedViewModel) {
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
                        daftarSubMateri.add(SubMateri(judul, gambar, jenis, isi, getBackgroundColor(id.toInt()), deskripsigambar, id, 0))
                    }
                    for (h in daftarSubMateri) {
                        if (h.judul.toLowerCase().contains(parameter.toLowerCase())) {
                            daftarSubMateriFiltered.add(h)
                        }
                    }
                    if (parameter == "") {
                        changeView(3)
                        viewModel.selectedTab("backtohome")
                    } else {
                        if (daftarSubMateriFiltered.size == 0) {
                            changeView(1)
                            viewModel.selectedTab("searching")
                        } else {
                            changeView(2)
                            viewModel.selectedTab("searching")
                        }
                    }
                    val adapter = AdapterSub(daftarSubMateriFiltered, this@EdukasiFragment.activity!!, 0, viewModel)
                    adapter.notifyDataSetChanged()
                    val tekstotal = "Total ${daftarSubMateriFiltered.size.toString()}"
                    view.teksTotalEdukasi.text = tekstotal
                    view.rvHasilSearch.adapter = adapter
                }
            }
        })
    }

    private fun getName() {
        ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.orderByKey().equalTo(auth.currentUser?.uid!!).addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    for (h in p0.children) {
                        val name = h.child("nama").value.toString()
                        root.edukasiHei.text = "Hai $name,"
                    }
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
        val const = ConstraintSet()
        const.clone(edukasiConst)
        const.connect(R.id.teksCopyright, ConstraintSet.TOP, R.id.teksKosong, ConstraintSet.BOTTOM, 22)
        const.applyTo(edukasiConst)
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
        root.edukasiDaftar.text = "Ini materi yang dapat kami temukan"
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
        root.edukasiDaftar.text = "Ini daftar topik edukasi yang tersedia"
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

    private fun getVoice() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        }
        startActivityForResult(intent, SPEECH_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            val spokenText: String? =
                data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).let { results ->
                    results?.get(0)
                }
            // Do something with spokenText
            root.editTextSearch.setText(spokenText)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onStop() {
        super.onStop()
        viewModel = activity?.run {
            ViewModelProviders.of(this)[SharedViewModel::class.java]
        } ?: throw Exception("Invalid Activity")
        viewModel.selectedTab("backtohome")
    }
}
