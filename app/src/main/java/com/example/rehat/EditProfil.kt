package com.example.rehat

import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_edit_profil.*


class EditProfil : AppCompatActivity() {

    private lateinit var ref: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profil)

        auth = FirebaseAuth.getInstance()

        btnSimpanPerubahan.setOnClickListener {
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

            if (namalengkap.isEmpty() || namapengguna.isEmpty() || email.isEmpty() || gender == "") {
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
    }
}
