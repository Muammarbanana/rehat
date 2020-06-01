package com.example.rehat.fragmenthome

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders

import com.example.rehat.R
import com.example.rehat.viewmodel.SharedViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_edit_profile.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class EditProfileFragment : Fragment() {

    private lateinit var root: View
    private lateinit var ref: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private var day = 0
    private var month = 0
    private var year = 0
    private val format = "dd MMMM yyyy"
    private val numformat = "dd-MM-yyyy"
    private val sdf = SimpleDateFormat(format, Locale.getDefault())
    private val sdfnum = SimpleDateFormat(numformat, Locale.getDefault())
    private val SPEECH_REQUEST_CODE = 0
    private var inputIdentificator = 0
    private lateinit var viewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_edit_profile, container, false)

        auth = FirebaseAuth.getInstance()

        viewModel = activity?.run {
            ViewModelProviders.of(this)[SharedViewModel::class.java]
        } ?: throw Exception("Invalid Activity")

        viewModel.selectedTab("editprofil")

        // Date
        val c = Calendar.getInstance()
        root.btnDate.text = sdf.format(c.time)
        var birthdate = sdfnum.format(c.time)
        year = c.get(Calendar.YEAR)
        month = c.get(Calendar.MONTH)
        day = c.get(Calendar.DAY_OF_MONTH)

        getData()

        root.btnDate.setOnClickListener {
            val dpd = DatePickerDialog(root.context, DatePickerDialog.OnDateSetListener { view, mYear, mMonth, mDay ->
                c.set(Calendar.YEAR, mYear)
                c.set(Calendar.MONTH, mMonth)
                c.set(Calendar.DAY_OF_MONTH, mDay)
                root.btnDate.text = sdf.format(c.time)
                birthdate = sdfnum.format(c.time)
            }, year, month, day)
            dpd.show()
        }

        root.btnSimpanPerubahan.setOnClickListener {
            editProfil(birthdate, viewModel)
        }

        root.micName.setOnClickListener {inputIdentificator = 0; getVoice() }
        root.micUsername.setOnClickListener { inputIdentificator = 1; getVoice() }
        root.micEmail.setOnClickListener { inputIdentificator = 2; getVoice() }
        root.micPass.setOnClickListener { inputIdentificator = 3; getVoice() }
        root.micKonf.setOnClickListener { inputIdentificator = 4; getVoice() }

        return root
    }

    private fun editProfil(birthdate: String, viewModel: SharedViewModel) {
        ref = FirebaseDatabase.getInstance().getReference("Users").child(auth.currentUser?.uid!!)
        val namalengkap = root.editTextNamaLengkap.text.toString()
        val namapengguna = root.editTextNamaPengguna.text.toString()
        val email = root.editTextEmail.text.toString()
        val id = root.rgJenisKelamin.checkedRadioButtonId
        var gender = ""
        val katasandi = root.editKataSandi.text.toString()
        val katasandikon = root.editKonfirmasiKataSandi.text.toString()

        if (id != -1) {
            val radio: RadioButton = root.findViewById(id)
            gender = radio.text.toString()
        }

        if (namalengkap.isEmpty() || namapengguna.isEmpty() || email.isEmpty()) {
            Toast.makeText(root.context, "Tidak boleh ada kolom yang kosong", Toast.LENGTH_SHORT).show()
        } else if (katasandi != katasandikon) {
            Toast.makeText(root.context, "Konfirmasi kata sandi tidak cocok", Toast.LENGTH_SHORT).show()
        } else {
            auth.currentUser?.updateEmail(email)
                ?.addOnCompleteListener(OnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if (katasandi.isNotEmpty()) {
                            auth.currentUser?.updatePassword(katasandi)?.addOnCompleteListener( OnCompleteListener { task2 ->
                                if (task2.isSuccessful) {
                                    ref.child("email").setValue(email)
                                    ref.child("username").setValue(namapengguna)
                                    ref.child("nama").setValue(namalengkap)
                                    ref.child("gender").setValue(gender)
                                    ref.child("birth").setValue(birthdate)
                                    Toast.makeText(root.context, "Edit profil berhasil", Toast.LENGTH_SHORT).show()
                                    activity?.supportFragmentManager?.popBackStack()
                                    viewModel.selectedTab("backtohomeprofil")
                                } else {
                                    Toast.makeText(
                                        root.context,
                                        "Edit profil gagal, silakan keluar dahulu, lalu coba lagi",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            })
                        } else {
                            ref.child("email").setValue(email)
                            ref.child("username").setValue(namapengguna)
                            ref.child("nama").setValue(namalengkap)
                            ref.child("gender").setValue(gender)
                            ref.child("birth").setValue(birthdate)
                            Toast.makeText(root.context, "Edit profil berhasil", Toast.LENGTH_SHORT).show()
                            activity?.supportFragmentManager?.popBackStack()
                            viewModel.selectedTab("backtohomeprofil")
                        }
                    } else {
                        Toast.makeText(
                            root.context,
                            "Edit profil gagal, silakan keluar dahulu, lalu coba lagi",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }
    }

    private fun getData() {
        ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.orderByKey().equalTo(auth.currentUser?.uid!!).addListenerForSingleValueEvent(object:
            ValueEventListener {
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
                            root.btnDate.text = sdf.format(c.time)
                        }
                        root.editTextNamaLengkap.setText(name)
                        root.editTextNamaPengguna.setText(username)
                        root.editTextEmail.setText(email)
                    }
                }
            }
        })
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
                0 -> root.editTextNamaLengkap.setText(spokenText)
                1 -> root.editTextNamaPengguna.setText(spokenText)
                2 -> root.editTextEmail.setText(spokenText)
                3 -> root.editKataSandi.setText(spokenText)
                else -> root.editKonfirmasiKataSandi.setText(spokenText)
            }

        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
