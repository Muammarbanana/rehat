package com.example.rehat.fragmenthome

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.rehat.R
import com.example.rehat.WelcomeScreen
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_profile.view.*

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        auth = FirebaseAuth.getInstance()
        view.teksKeluar.setOnClickListener {
            auth.signOut()
            Toast.makeText(context, "Berhasil Keluar", Toast.LENGTH_SHORT).show()
            var intent = Intent(context, WelcomeScreen::class.java)
            intent = intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent = intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
        return view
    }

}
