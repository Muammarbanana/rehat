package com.example.rehat

import android.os.Bundle
import android.util.Log
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
            val email = editTextEmail.text.toString()
            auth.currentUser?.updateEmail(email)?.addOnCompleteListener(this, OnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Ubah email berhasil", Toast.LENGTH_SHORT).show()
                    ref = FirebaseDatabase.getInstance().getReference("Users")
                    ref.child(auth.currentUser?.uid!!).child("email").setValue(email)
                    finish()
                } else {
                    Toast.makeText(this, "Ubah email gagal, silakan keluar dahulu, lalu coba lagi", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
