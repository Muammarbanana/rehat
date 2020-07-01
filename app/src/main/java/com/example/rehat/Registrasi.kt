package com.example.rehat

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
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
        imgMicrophone6.setOnClickListener {
            inputIdentificator = 3
            getVoice()
        }
    }

    private fun registerUser() {
        val nama = editTextNamaLengkap.text.toString()
        val namapengguna = editTextNamaPengguna.text.toString()
        val email = "${namapengguna}@gmail.com"
        val password = editTextKataSandi1.text.toString()

        if (namapengguna.isEmpty()) {
            customToast("Pendaftaran gagal, mohon isi nama pengguna terlebih dahulu")
        } else if (email.isEmpty()) {
            customToast("Pendaftaran gagal, mohon isi email terlebih dahulu")
        } else if (password.isEmpty()){
            customToast("Pendaftaran gagal, mohon isi kata sandi terlebih dahulu")
        } else {
            if (namapengguna.contains(" ")) {
                customToast("Terjadi kesalahan, nama pengguna yang kamu masukkan mengandung spasi mohon periksa kembali")
            } else if (email.contains(" ")) {
                customToast("Terjadi kesalahan, email yang kamu masukkan mengandung spasi mohon periksa kembali")
            } else if (password.contains(" ")) {
                customToast("Terjadi kesalahan, kata sandi yang kamu masukkan mengandung spasi mohon periksa kembali")
            } else {
                showLoading()
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user =
                            User(nama, namapengguna, email)
                        ref.child(FirebaseAuth.getInstance().currentUser!!.uid)
                            .setValue(user)
                        ref.child(FirebaseAuth.getInstance().currentUser!!.uid).child("emaildummy").setValue(1)
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
                3 -> editTextKataSandi1.setText(spokenText)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    // Fungsi klik
    fun getBack(view: View) {
        finish()
    }

    private fun showLoading() {
        val toastLayout = layoutInflater.inflate(R.layout.toast_layout_invisible, findViewById(R.id.constToast))
        val toast = Toast(this)
        toastLayout.textToast.text = "Sedang memproses untuk registrasi"
        toast.duration = Toast.LENGTH_SHORT
        toast.view = toastLayout
        toast.show()
        constRegistrasi.isEnabled = false
        linearMaskingRegis.visibility = View.VISIBLE
        progressBarRegistrasi.progress = 0
        progressBarRegistrasi.secondaryProgress = 100
        progressBarRegistrasi.max = 100
        progressBarRegistrasi.visibility = View.VISIBLE
        teksLoadingRegistrasi.visibility = View.VISIBLE
        val handler = Handler()
        Thread(Runnable {
            var pStatus = 0
            while (pStatus < 100) {
                pStatus += 1;

                handler.post {
                    progressBarRegistrasi.progress = pStatus;
                    teksLoadingRegistrasi.text = "$pStatus %";
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
        constRegistrasi.isEnabled = true
        linearMaskingRegis.visibility = View.GONE
        progressBarRegistrasi.visibility = View.GONE
        teksLoadingRegistrasi.visibility = View.GONE
    }

    private fun customToast(text: String) {
        val toastLayout = layoutInflater.inflate(R.layout.toast_layout, findViewById(R.id.constToast))
        val toast = Toast(this)
        toastLayout.textToast.text = text
        toast.duration = Toast.LENGTH_SHORT
        toast.view = toastLayout
        toast.show()
    }
}
