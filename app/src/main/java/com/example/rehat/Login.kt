package com.example.rehat

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.provider.Settings.Global.putString
import android.speech.RecognizerIntent
import android.support.v7.app.AppCompatActivity
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_login.*

class Login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var ref: DatabaseReference
    private val SPEECH_REQUEST_CODE = 0
    private var inputIdentificator = 0

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
            passUsername(object : MyCallBack {
                override fun onCallBack(email: String) {
                    loginUser(email, password)
                }
            }, namaPengguna)
        }

        val et: EditText = findViewById(R.id.editTextNamaPengguna)

        imgMicrophone1.setOnClickListener {
            inputIdentificator = 0
            getVoice()
        }

        imgMicrophone2.setOnClickListener {
            inputIdentificator = 1
            getVoice()
        }
    }

    // Check if user is logged in or not
    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            startActivity(Intent(this,Home::class.java))
            finish()
        }
    }

    // Login with email and password
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

    // Read username from database and get the user email for login
    private fun passUsername(firebasecallback: MyCallBack, username: String) {
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

    // Get email outside of onDataChange method
    private interface MyCallBack {
        fun onCallBack(text: String)
    }

    private fun getVoice() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        }
        startActivityForResult(intent, SPEECH_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val spokenText: String? =
                data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).let { results ->
                    results?.get(0)
                }
            // Do something with spokenText
            when(inputIdentificator) {
                0 -> editTextNamaPengguna.setText(spokenText)
                else -> editTextKataSandi.setText(spokenText)
            }

        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}