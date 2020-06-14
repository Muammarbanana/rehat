package com.example.rehat

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.speech.RecognizerIntent
import android.view.View
import android.widget.Toast
import com.example.rehat.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.toast_layout.view.*


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
                val toastLayout = layoutInflater.inflate(R.layout.toast_layout, findViewById(R.id.constToast))
                val toast = Toast(this)
                toastLayout.textToast.text = "Tolong isi nama pengguna dan kata sandi"
                toast.duration = Toast.LENGTH_SHORT
                toast.view = toastLayout
                toast.show()
                return@setOnClickListener
            }
            passUsername(object : MyCallBack {
                override fun onCallBack(email: String) {
                    loginUser(email, password)
                }
            }, namaPengguna)
            showLoading()
        }

        imgMicrophone1.setOnClickListener {
            // Identificate which input is handled
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
            startActivity(Intent(this, Home::class.java))
            finish()
        }
    }

    private fun showLoading() {
        constLogin.isEnabled = false
        linearMasking.visibility = View.VISIBLE
        progressBarLogin.progress = 0
        progressBarLogin.secondaryProgress = 100
        progressBarLogin.max = 100
        progressBarLogin.visibility = View.VISIBLE
        teksLoading.visibility = View.VISIBLE
        val handler = Handler()
        Thread(Runnable {
            var pStatus = 0
            while (pStatus < 100) {
                pStatus += 1;

                handler.post {
                    progressBarLogin.progress = pStatus;
                    teksLoading.text = "$pStatus %";
                }
                try {
                    Thread.sleep(15)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }).start()
    }

    private fun removeLoading() {
        constLogin.isEnabled = true
        linearMasking.visibility = View.GONE
        progressBarLogin.visibility = View.GONE
        teksLoading.visibility = View.GONE
    }

    // Login with email and password
    private fun loginUser(email: String, password: String) {
        if (email.isNotEmpty()) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        val intent = Intent(this, Home::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    } else {
                        removeLoading()
                        val toastLayout = layoutInflater.inflate(R.layout.toast_layout, findViewById(R.id.constToast))
                        val toast = Toast(this)
                        toastLayout.textToast.text = "Nama pengguna atau kata sandi salah, silakan coba lagi"
                        toast.duration = Toast.LENGTH_SHORT
                        toast.view = toastLayout
                        toast.show()
                    }
                }
        } else {
            removeLoading()
            val toastLayout = layoutInflater.inflate(R.layout.toast_layout, findViewById(R.id.constToast))
            val toast = Toast(this)
            toastLayout.textToast.text = "Nama pengguna atau kata sandi salah, silakan coba lagi"
            toast.duration = Toast.LENGTH_SHORT
            toast.view = toastLayout
            toast.show()
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
        fun onCallBack(email: String)
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
                0 -> editTextNamaPengguna.setText(spokenText)
                else -> editTextKataSandi.setText(spokenText)
            }

        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    // Fungsi klik
    fun getBack(view: View) {
        finish()
    }
}