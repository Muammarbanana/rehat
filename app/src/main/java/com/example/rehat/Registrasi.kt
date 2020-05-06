package com.example.rehat

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.support.v7.app.AppCompatActivity
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_registrasi.*
import kotlinx.android.synthetic.main.activity_registrasi.editTextNamaPengguna
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit


class Registrasi : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    lateinit var ref : DatabaseReference
    private val SPEECH_REQUEST_CODE = 0
    private var inputIdentificator = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrasi)

        auth = FirebaseAuth.getInstance()
        ref = FirebaseDatabase.getInstance().getReference("Users")

        backRegis.setOnClickListener { finish() }
        regisButton.setOnClickListener {
            registerUser()
        }
        imgMicrophone3.setOnClickListener {
            inputIdentificator = 0
            getVoice()
        }
        imgMicrophone4.setOnClickListener {
            inputIdentificator = 1
            getVoice()
        }
        imgMicrophone5.setOnClickListener {
            inputIdentificator = 2
            getVoice()
        }
        imgMicrophone6.setOnClickListener {
            inputIdentificator = 3
            getVoice()
        }
        imgMicrophone7.setOnClickListener {
            inputIdentificator = 4
            getVoice()
        }
    }

    private fun registerUser() {
        val nama = editTextNamaLengkap.text.toString()
        val namapengguna = editTextNamaPengguna.text.toString()
        val email = editTextEmail.text.toString()
        val password = editTextKataSandi1.text.toString()
        val passwordUlang = editTextKonfirmasiKataSandi.text.toString()

        if (nama.isEmpty() || namapengguna.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Tidak boleh ada kolom yang kosong", Toast.LENGTH_SHORT).show()
        } else if (passwordUlang!=password) {
            Toast.makeText(this, "Konfirmasi kata sandi tidak cocok", Toast.LENGTH_SHORT).show()
        } else {
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = User(nama, namapengguna, email)
                    ref.child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .setValue(user)
                    auth.signOut()
                    Toast.makeText(this, "Registrasi Berhasil", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, Login::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Registrasi Gagal", Toast.LENGTH_SHORT).show()
                }
            }
        }
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
                0 -> editTextNamaLengkap.setText(spokenText)
                1 -> editTextNamaPengguna.setText(spokenText)
                2 -> editTextEmail.setText(spokenText)
                3 -> editTextKataSandi1.setText(spokenText)
                else -> editTextKonfirmasiKataSandi.setText(spokenText)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onResume() {
        super.onResume()
        val worker: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
        val dataElemen = listOf(
            regisNamaLengkap,
            editTextNamaLengkap,
            imgMicrophone3,
            regisNamaPengguna,
            editTextNamaPengguna,
            imgMicrophone4,
            regisEmail,
            editTextEmail,
            imgMicrophone5,
            regisKataSandi,
            editTextKataSandi1,
            imgMicrophone6,
            regisKonfirmasiKataSandi,
            editTextKonfirmasiKataSandi,
            imgMicrophone7,
            regisJaminan
        )
        var task: Runnable
        var orderTime = longArrayOf(5, 7, 22, 28, 30, 45, 51, 53, 69, 75, 77, 93, 99, 101, 117, 123)
        var loopCount = 0
        for (x in dataElemen) {
            if (loopCount == 0 || loopCount == 3 || loopCount == 6 || loopCount == 9 || loopCount == 12 || loopCount == 15) {
                task = Runnable { x.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED) }
            } else {
                task = Runnable { x.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED) }
                x.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_SCROLLED)
            }
            worker.schedule(task, orderTime[loopCount], TimeUnit.SECONDS)
            loopCount += 1
        }
    }
}
