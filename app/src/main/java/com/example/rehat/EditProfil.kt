package com.example.rehat

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.example.rehat.viewmodel.SharedViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_edit_profil.*
import kotlinx.android.synthetic.main.toast_layout.view.*
import java.text.SimpleDateFormat
import java.util.*

class EditProfil : AppCompatActivity() {

    private lateinit var ref: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private var day = 0
    private var month = 0
    private var year = 0
    private val format = "dd MMMM yyyy"
    private val numformat = "dd-MM-yyyy"
    private val sdf = SimpleDateFormat(format, Locale.getDefault())
    private val sdfnum = SimpleDateFormat(numformat, Locale.getDefault())
    private val SPEECH_REQUEST_CODE = 0
    private var inputIdentificator = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profil)

        auth = FirebaseAuth.getInstance()

        // Date
        val c = Calendar.getInstance()
        btnDate.text = sdf.format(c.time)
        var birthdate = sdfnum.format(c.time)
        year = c.get(Calendar.YEAR)
        month = c.get(Calendar.MONTH)
        day = c.get(Calendar.DAY_OF_MONTH)

        getData()

        btnDate.setOnClickListener {
            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, mYear, mMonth, mDay ->
                c.set(Calendar.YEAR, mYear)
                c.set(Calendar.MONTH, mMonth)
                c.set(Calendar.DAY_OF_MONTH, mDay)
                btnDate.text = sdf.format(c.time)
                birthdate = sdfnum.format(c.time)
            }, year, month, day)
            dpd.show()
            btnDate.setTextColor(Color.parseColor("#172B4D"))
        }

        btnSimpanPerubahan.setOnClickListener {
            editProfil(birthdate)
        }

        imgBackEditProfil.setOnClickListener { finish() }

        micName.setOnClickListener {inputIdentificator = 0; getVoice() }
        micUsername.setOnClickListener { inputIdentificator = 1; getVoice() }
        micEmail.setOnClickListener { inputIdentificator = 2; getVoice() }
        micPass.setOnClickListener { inputIdentificator = 3; getVoice() }
        micKonf.setOnClickListener { inputIdentificator = 4; getVoice() }
    }

    private fun editProfil(birthdate: String) {
        ref = FirebaseDatabase.getInstance().getReference("Users").child(auth.currentUser?.uid!!)
        val namalengkap = editTextNamaLengkap.text.toString()
        val namapengguna = editTextNamaPengguna.text.toString()
        val email = editTextEmail.text.toString()
        val id = rgJenisKelamin.checkedRadioButtonId
        var gender = ""
        val katasandi = editKataSandi.text.toString()
        val katasandikon = editKonfirmasiKataSandi.text.toString()

        if (id != -1) {
            val radio: RadioButton = findViewById(id)
            gender = radio.text.toString()
        }

        if (namalengkap.isEmpty() || namapengguna.isEmpty() || email.isEmpty()) {
            val toastLayout = layoutInflater.inflate(R.layout.toast_layout, findViewById(R.id.constToast))
            val toast = Toast(this)
            toastLayout.textToast.text = "Tidak boleh ada kolom yang kosong"
            toast.duration = Toast.LENGTH_SHORT
            toast.view = toastLayout
            toast.show()
        } else if (katasandi != katasandikon) {
            val toastLayout = layoutInflater.inflate(R.layout.toast_layout, findViewById(R.id.constToast))
            val toast = Toast(this)
            toastLayout.textToast.text = "Konfirmasi kata sandi tidak cocok"
            toast.duration = Toast.LENGTH_SHORT
            toast.view = toastLayout
            toast.show()
        } else {
            auth.currentUser?.updateEmail(email)
                ?.addOnCompleteListener(this, OnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if (katasandi.isNotEmpty()) {
                            auth.currentUser?.updatePassword(katasandi)?.addOnCompleteListener(this, OnCompleteListener { task2 ->
                                if (task2.isSuccessful) {
                                    ref.child("email").setValue(email)
                                    ref.child("username").setValue(namapengguna)
                                    ref.child("nama").setValue(namalengkap)
                                    ref.child("gender").setValue(gender)
                                    ref.child("birth").setValue(birthdate)
                                    val toastLayout = layoutInflater.inflate(R.layout.toast_layout, findViewById(R.id.constToast))
                                    val toast = Toast(this)
                                    toastLayout.textToast.text = "Perubahan berhasil disimpan"
                                    toast.duration = Toast.LENGTH_SHORT
                                    toast.view = toastLayout
                                    toast.show()
                                    finish()
                                } else {
                                    val toastLayout = layoutInflater.inflate(R.layout.toast_layout, findViewById(R.id.constToast))
                                    val toast = Toast(this)
                                    toastLayout.textToast.text = "Edit profil gagal, silakan keluar dahulu, lalu coba lagi"
                                    toast.duration = Toast.LENGTH_SHORT
                                    toast.view = toastLayout
                                    toast.show()
                                }
                            })
                        } else {
                            ref.child("email").setValue(email)
                            ref.child("username").setValue(namapengguna)
                            ref.child("nama").setValue(namalengkap)
                            ref.child("gender").setValue(gender)
                            ref.child("birth").setValue(birthdate)
                            val toastLayout = layoutInflater.inflate(R.layout.toast_layout, findViewById(R.id.constToast))
                            val toast = Toast(this)
                            toastLayout.textToast.text = "Perubahan berhasil disimpan"
                            toast.duration = Toast.LENGTH_SHORT
                            toast.view = toastLayout
                            toast.show()
                            finish()
                        }
                    } else {
                        val toastLayout = layoutInflater.inflate(R.layout.toast_layout, findViewById(R.id.constToast))
                        val toast = Toast(this)
                        toastLayout.textToast.text = "Edit profil gagal, silakan keluar dahulu, lalu coba lagi"
                        toast.duration = Toast.LENGTH_SHORT
                        toast.view = toastLayout
                        toast.show()
                    }
                })
        }
    }

    private fun getData() {
        ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.orderByKey().equalTo(auth.currentUser?.uid!!).addListenerForSingleValueEvent(object:
            ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    for (h in p0.children) {
                        val name = h.child("nama").value.toString()
                        val username = h.child("username").value.toString()
                        val email = h.child("email").value.toString()
                        val birth = h.child("birth").value.toString()
                        val arrdate = birth.split("-")
                        val gender = h.child("gender").value.toString()
                        if (birth != "null") {
                            day = arrdate[0].toInt()
                            month = arrdate[1].toInt() - 1
                            year = arrdate[2].toInt()
                            val c = Calendar.getInstance()
                            c.set(Calendar.YEAR, year)
                            c.set(Calendar.MONTH, month)
                            c.set(Calendar.DAY_OF_MONTH, day)
                            btnDate.text = sdf.format(c.time)
                            btnDate.setTextColor(Color.parseColor("#172B4D"))
                        }
                        editTextNamaLengkap.setText(name)
                        editTextNamaPengguna.setText(username)
                        editTextEmail.setText(email)
                        when (gender) {
                            "Perempuan" -> rgJenisKelamin.check(R.id.rbPerem)
                            "Laki-Laki" -> rgJenisKelamin.check(R.id.rbLaki)
                        }
                    }
                }
            }
        })
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
            when(inputIdentificator) {
                0 -> editTextNamaLengkap.setText(spokenText)
                1 -> editTextNamaPengguna.setText(spokenText)
                2 -> editTextEmail.setText(spokenText)
                3 -> editKataSandi.setText(spokenText)
                else -> editKonfirmasiKataSandi.setText(spokenText)
            }

        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
