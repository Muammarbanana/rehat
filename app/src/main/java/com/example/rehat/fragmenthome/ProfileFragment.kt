package com.example.rehat.fragmenthome

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.rehat.R
import com.example.rehat.WelcomeScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.fragment_profile.view.profileName
import kotlinx.android.synthetic.main.pop_alert.view.*

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var ref: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        auth = FirebaseAuth.getInstance()
        view.teksKeluar.setOnClickListener {
            popAlert(view)
        }
        getName(view)

        return view
    }

    private fun popAlert(view: View) {
        val dialog = AlertDialog.Builder(view.context).create()
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.pop_alert, null)
        dialog.setView(dialogView)
        dialog.setCancelable(true)
        dialogView.alertText.text = "Apakah kamu yakin ingin keluar akun?"
        dialogView.btnAccept.text = "Ya, Keluar"
        dialogView.btnAccept.setTextColor(Color.parseColor("#DB4437"))
        dialogView.btnCancel.setOnClickListener { dialog.dismiss() }
        dialogView.btnAccept.setOnClickListener {
            signOut(view)
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun signOut(view: View) {
        auth.signOut()
        Toast.makeText(context, "Berhasil Keluar", Toast.LENGTH_SHORT).show()
        var intent = Intent(context, WelcomeScreen::class.java)
        intent = intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent = intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        view.context.startActivity(intent)
    }

    private fun getName(view: View) {
        ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.orderByKey().equalTo(auth.currentUser?.uid!!).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    for (h in p0.children) {
                        val name = h.child("nama").value.toString()
                        val photo = h.child("photo").value.toString()
                        profileName.text = name
                        Picasso.get().load(photo).into(profilPic)
                    }
                }
            }
        })
    }

}
