package com.example.rehat.fragmenthome

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.rehat.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

/**
 * A simple [Fragment] subclass.
 */
class NotifikasiFragment : Fragment() {

    private lateinit var root: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_notifikasi, container, false)

        getDataNotifikasi()

        return root
    }

    private fun getDataNotifikasi() {
        val ref = FirebaseDatabase.getInstance().getReference("notifikasi")
        ref.orderByChild("iduser").equalTo(FirebaseAuth.getInstance().uid).addValueEventListener(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    val transaction = activity?.supportFragmentManager?.beginTransaction()
                    transaction?.replace(R.id.frameNotifikasi, NotifikasiIsiFragment())
                    transaction?.commitAllowingStateLoss()
                }
            }

        })
    }

}
