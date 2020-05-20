package com.example.rehat

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_edit_profil.*
import java.text.SimpleDateFormat
import java.util.*
import javax.xml.datatype.DatatypeConstants.MONTHS


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
        }

        btnSimpanPerubahan.setOnClickListener {
            editProfil(birthdate)
        }
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

        if (namalengkap.isEmpty() || namapengguna.isEmpty() || email.isEmpty() || gender == "" || katasandi.isEmpty()) {
            Toast.makeText(this, "Tidak boleh ada kolom yang kosong", Toast.LENGTH_SHORT).show()
        } else if (katasandi != katasandikon) {
            Toast.makeText(this, "Konfirmasi kata sandi tidak cocok", Toast.LENGTH_SHORT).show()
        } else {
            auth.currentUser?.updateEmail(email)
                ?.addOnCompleteListener(this, OnCompleteListener { task ->
                    if (task.isSuccessful) {
                        ref.child("email").setValue(email)
                        ref.child("username").setValue(namapengguna)
                        ref.child("nama").setValue(namalengkap)
                        ref.child("gender").setValue(gender)
                        ref.child("birth").setValue(birthdate)
                        auth.currentUser?.updatePassword(katasandi)
                        Toast.makeText(this, "Edit profil berhasil", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(
                            this,
                            "Edit profil gagal, silakan keluar dahulu, lalu coba lagi",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }
    }

    private fun getData() {
        ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.orderByKey().equalTo(auth.currentUser?.uid!!).addListenerForSingleValueEvent(object: ValueEventListener {
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
                        if (birth != "null") {
                            day = arrdate[0].toInt()
                            month = arrdate[1].toInt() - 1
                            year = arrdate[2].toInt()
                            val c = Calendar.getInstance()
                            c.set(Calendar.YEAR, year)
                            c.set(Calendar.MONTH, month)
                            c.set(Calendar.DAY_OF_MONTH, day)
                            btnDate.text = sdf.format(c.time)
                        }
                        editTextNamaLengkap.setText(name)
                        editTextNamaPengguna.setText(username)
                        editTextEmail.setText(email)
                    }
                }
            }
        })
    }
}
