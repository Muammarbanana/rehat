package com.example.rehat.fragmenthome

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintSet
import com.example.rehat.EditProfil
import com.example.rehat.R
import com.example.rehat.WelcomeScreen
import com.example.rehat.viewmodel.SharedViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.fragment_profile.view.profilPic
import kotlinx.android.synthetic.main.fragment_profile.view.profileName
import kotlinx.android.synthetic.main.pop_alert.view.*
import kotlinx.android.synthetic.main.toast_layout.view.*

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var ref: DatabaseReference
    private lateinit var storage: StorageReference
    private lateinit var root: View
    private lateinit var imageUri: Uri
    private val PICK_IMAGE_REQUEST = 1
    private lateinit var viewModel: SharedViewModel

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
            startActivity(intent)
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
        dialogView.cardAlert.radius = 10F
        dialogView.alertText.text = "Apakah kamu yakin ingin keluar akun?"
        dialogView.btnAccept.text = "Ya, Keluar"
        dialogView.btnAccept.setTextColor(Color.parseColor("#DB4437"))
        dialogView.btnCancel.setOnClickListener { dialog.dismiss() }
        dialogView.btnAccept.setOnClickListener {
            signOut(view)
            val toastLayout = layoutInflater.inflate(R.layout.toast_layout, view.findViewById(R.id.constToast))
            val toast = Toast(view.context)
            toastLayout.textToast.text = "Berhasil Keluar"
            toast.duration = Toast.LENGTH_SHORT
            toast.view = toastLayout
            toast.show()
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun signOut(view: View) {
        val uid = auth.currentUser?.uid!!
        auth.signOut()
        var intent = Intent(context, WelcomeScreen::class.java)
        intent = intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent = intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        view.context.startActivity(intent)
        val ref = FirebaseDatabase.getInstance().getReference("tokendevice/$uid")
        ref.removeValue()
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
                        if (photo == "null" || photo == "") {
                            profilPic.contentDescription = "Belum memasang foto profil"
                        } else {
                            Picasso.get().load(photo).resize(90, 90).into(root.profilPic)
                            profilPic.contentDescription = ""
                        }
                        if (gender == "null" || birth == "null" || gender == "") {
                            val const = ConstraintSet()
                            const.clone(root.constProfil)
                            const.connect(R.id.teksEditProfile, ConstraintSet.TOP, R.id.guideline41, ConstraintSet.TOP)
                            const.connect(R.id.teksEditProfile, ConstraintSet.BOTTOM, R.id.guideline42, ConstraintSet.TOP)
                            const.connect(R.id.ikonEditProfil, ConstraintSet.TOP, R.id.guideline41, ConstraintSet.TOP)
                            const.connect(R.id.ikonEditProfil, ConstraintSet.BOTTOM, R.id.guideline42, ConstraintSet.TOP)
                            const.connect(R.id.teksHubungiKami, ConstraintSet.TOP, R.id.guideline42, ConstraintSet.BOTTOM)
                            const.connect(R.id.teksHubungiKami, ConstraintSet.BOTTOM, R.id.guideline43, ConstraintSet.TOP)
                            const.connect(R.id.ikonHubungi, ConstraintSet.TOP, R.id.guideline42, ConstraintSet.BOTTOM)
                            const.connect(R.id.ikonHubungi, ConstraintSet.BOTTOM, R.id.guideline43, ConstraintSet.TOP)
                            const.connect(R.id.teksSyarat, ConstraintSet.TOP, R.id.guideline43, ConstraintSet.BOTTOM)
                            const.connect(R.id.teksSyarat, ConstraintSet.BOTTOM, R.id.guideline44, ConstraintSet.TOP)
                            const.connect(R.id.ikonSyarat, ConstraintSet.TOP, R.id.guideline43, ConstraintSet.BOTTOM)
                            const.connect(R.id.ikonSyarat, ConstraintSet.BOTTOM, R.id.guideline44, ConstraintSet.TOP)
                            const.setVerticalBias(R.id.teksEditProfile, 0.5F)
                            const.setVerticalBias(R.id.ikonEditProfil, 0.5F)
                            const.applyTo(root.constProfil)
                            root.guideline41.setGuidelinePercent(0.14991F)
                            root.lengkapiProfil.visibility = View.VISIBLE
                            root.imgWarning.visibility = View.VISIBLE
                            root.view3.visibility = View.VISIBLE
                        } else {
                            val const = ConstraintSet()
                            const.clone(root.constProfil)
                            const.connect(R.id.teksEditProfile, ConstraintSet.TOP, R.id.constProfil, ConstraintSet.TOP)
                            const.connect(R.id.teksEditProfile, ConstraintSet.BOTTOM, R.id.guideline41, ConstraintSet.TOP)
                            const.connect(R.id.ikonEditProfil, ConstraintSet.TOP, R.id.constProfil, ConstraintSet.TOP)
                            const.connect(R.id.ikonEditProfil, ConstraintSet.BOTTOM, R.id.guideline41, ConstraintSet.TOP)
                            const.connect(R.id.teksHubungiKami, ConstraintSet.TOP, R.id.guideline41, ConstraintSet.BOTTOM)
                            const.connect(R.id.teksHubungiKami, ConstraintSet.BOTTOM, R.id.guideline42, ConstraintSet.TOP)
                            const.connect(R.id.ikonHubungi, ConstraintSet.TOP, R.id.guideline41, ConstraintSet.BOTTOM)
                            const.connect(R.id.ikonHubungi, ConstraintSet.BOTTOM, R.id.guideline42, ConstraintSet.TOP)
                            const.connect(R.id.teksSyarat, ConstraintSet.TOP, R.id.guideline42, ConstraintSet.BOTTOM)
                            const.connect(R.id.teksSyarat, ConstraintSet.BOTTOM, R.id.guideline43, ConstraintSet.TOP)
                            const.connect(R.id.ikonSyarat, ConstraintSet.TOP, R.id.guideline42, ConstraintSet.BOTTOM)
                            const.connect(R.id.ikonSyarat, ConstraintSet.BOTTOM, R.id.guideline43, ConstraintSet.TOP)
                            const.setVerticalBias(R.id.teksEditProfile, 0.7F)
                            const.setVerticalBias(R.id.ikonEditProfil, 0.7F)
                            const.applyTo(root.constProfil)
                            root.guideline41.setGuidelinePercent(0.1833F)
                            root.lengkapiProfil.visibility = View.GONE
                            root.imgWarning.visibility = View.GONE
                            root.view3.visibility = View.GONE
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
