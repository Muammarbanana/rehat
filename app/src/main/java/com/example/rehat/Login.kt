package com.example.rehat

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_login.*

class Login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var ref: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        backLogin.setOnClickListener {
            finish()
        }

        loginButton.setOnClickListener {
            val namaPengguna = editTextNamaPengguna.text.toString()
            val password = editTextKataSandi.text.toString()
            if (namaPengguna.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Tolong isi username dan password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            getUsernameList(object : FirebaseCallBack {
                override fun onCallBack(email: String) {
                    loginUser(email, password)
                }
            }, namaPengguna)
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            startActivity(Intent(this,Home::class.java))
            finish()
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(this, "Login Sukses", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, Home::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(baseContext, "Login Gagal", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun getUsernameList(firebasecallback: FirebaseCallBack, username: String) {
        val daftar = mutableListOf<User>()
        ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                var email = ""
                if(p0.exists()){
                    for(h in p0.children) {
                        val user = h.getValue(User::class.java)
                        daftar.add(user!!)
                    }
                }
                for(i in daftar) {
                    if (username.equals(i.username)) {
                        email = i.email
                    }
                }
                firebasecallback.onCallBack(email)
            }
        })
    }

    private interface FirebaseCallBack {
        fun onCallBack(email: String)
    }
}
