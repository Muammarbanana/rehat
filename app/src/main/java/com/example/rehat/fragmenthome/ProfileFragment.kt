package com.example.rehat.fragmenthome

import android.app.Activity.RESULT_OK
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.rehat.EditProfil
import com.example.rehat.R
import com.example.rehat.WelcomeScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
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
    private lateinit var storage: StorageReference
    private lateinit var root: View
    private lateinit var imageUri: Uri
    private val PICK_IMAGE_REQUEST = 1;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_profile, container, false)

        auth = FirebaseAuth.getInstance()
        root.teksKeluar.setOnClickListener {
            popAlert(root)
        }

        storage = FirebaseStorage.getInstance().getReference("profilepic")
        ref = FirebaseDatabase.getInstance().getReference("Users")

        getName(root)

        root.teksEditProfile.setOnClickListener {
            val intent = Intent(root.context, EditProfil::class.java)
            root.context.startActivity(intent)
        }

        root.imgAddPhoto.setOnClickListener {
            openImageChooser(root)
        }

        return root
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
        ref.orderByKey().equalTo(auth.currentUser?.uid!!).addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    for (h in p0.children) {
                        val name = h.child("nama").value.toString()
                        val photo = h.child("photo").value.toString()
                        val gender = h.child("gender").value.toString()
                        val birth = h.child("birth").value.toString()
                        root.profileName.text = name
                        if (gender == "null" || birth == "null") {
                            lengkapiProfil.visibility = View.VISIBLE
                            imgWarning.visibility = View.VISIBLE
                        } else {
                            Picasso.get().load(photo).into(root.profilPic)
                        }
                    }
                }
            }
        })
    }

    private fun openImageChooser(view: View){
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            imageUri = data.data!!
            uploadImage()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun uploadImage() {
        if (imageUri != null) {
            var donloturi = ""
            val filereference = storage.child(auth.currentUser?.uid.toString() + "." + getFileExtension(imageUri))
            filereference.putFile(imageUri)
            Handler().postDelayed({
                filereference.downloadUrl.addOnSuccessListener {
                    donloturi = it.toString()
                    ref.child(auth.currentUser?.uid!!).child("photo").setValue(donloturi)
                }
            }, 5000)
        } else {
            Toast.makeText(root.context, "Tidak ada fail yang dipilih", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getFileExtension(uri: Uri): String {
        val cr = root.context.contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cr.getType(uri))!!
    }
}
