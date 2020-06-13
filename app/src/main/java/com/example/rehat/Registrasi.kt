package com.example.rehat

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.View
import android.widget.Toast
import com.example.rehat.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_registrasi.*
import kotlinx.android.synthetic.main.activity_registrasi.editTextNamaPengguna
import kotlinx.android.synthetic.main.toast_layout.view.*


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
            val toastLayout = layoutInflater.inflate(R.layout.toast_layout, findViewById(R.id.constToast))
            val toast = Toast(this)
            toastLayout.textToast.text = "Tidak boleh ada kolom yang kosong"
            toast.duration = Toast.LENGTH_SHORT
            toast.view = toastLayout
            toast.show()
        } else if (passwordUlang!=password) {
            val toastLayout = layoutInflater.inflate(R.layout.toast_layout, findViewById(R.id.constToast))
            val toast = Toast(this)
            toastLayout.textToast.text = "Konfirmasi kata sandi tidak cocok"
            toast.duration = Toast.LENGTH_SHORT
            toast.view = toastLayout
            toast.show()
        } else {
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user =
                        User(nama, namapengguna, email)
                    ref.child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .setValue(user)
                    auth.signOut()
                    val toastLayout = layoutInflater.inflate(R.layout.toast_layout, findViewById(R.id.constToast))
                    val toast = Toast(this)
                    toastLayout.textToast.text = "Pendaftaran berhasil, silakan login"
                    toast.duration = Toast.LENGTH_SHORT
                    toast.view = toastLayout
                    toast.show()
                    startActivity(Intent(this, Login::class.java))
                    finish()
                } else {
                    val toastLayout = layoutInflater.inflate(R.layout.toast_layout, findViewById(R.id.constToast))
                    val toast = Toast(this)
                    toastLayout.textToast.text = "Pendaftaran gagal"
                    toast.duration = Toast.LENGTH_SHORT
                    toast.view = toastLayout
                    toast.show()
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
                3 -> editTextKataSandi1.setText(spokenText)
                else -> editTextKonfirmasiKataSandi.setText(spokenText)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    // Fungsi klik
    fun getBack(view: View) {
        finish()
    }
}
