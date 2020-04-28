package com.example.rehat

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_registrasi.*


class Registrasi : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    lateinit var ref : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrasi)

        auth = FirebaseAuth.getInstance()
        ref = FirebaseDatabase.getInstance().getReference("Users")

        backRegis.setOnClickListener { finish() }
        regisButton.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        val nama = editTextNamaLengkap.text.toString()
        val namapengguna = editTextNamaPengguna.text.toString()
        val email = editTextEmail.text.toString()
        val password = editTextKataSandi1.text.toString()

        if (nama.isEmpty()) {
            editTextNamaLengkap.error = "Nama lengkap tidak boleh kosong"
        }

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val user = User(nama, namapengguna, email)
                ref.child(FirebaseAuth.getInstance().currentUser!!.uid)
                    .setValue(user)
            } else {
                Toast.makeText(this, "Registrasi Gagal", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
