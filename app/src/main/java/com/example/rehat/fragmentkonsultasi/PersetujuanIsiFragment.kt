package com.example.rehat.fragmentkonsultasi

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.rehat.R
import com.example.rehat.RincianPersetujuan
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_persetujuan_isi.*
import kotlinx.android.synthetic.main.fragment_persetujuan_isi.view.*
import kotlinx.android.synthetic.main.pop_alert.view.*

/**
 * A simple [Fragment] subclass.
 */
class PersetujuanIsiFragment : Fragment() {

    private lateinit var ref: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var root: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_persetujuan_isi, container, false)

        auth = FirebaseAuth.getInstance()
        getDataJanji()
        getName()

        return root
    }

    private fun getDataJanji() {
        ref = FirebaseDatabase.getInstance().getReference("janji")
        ref.orderByChild("id_user").equalTo(auth.currentUser?.uid!!).addListenerForSingleValueEvent(object:
            ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    for (h in p0.children) {
                        val namadokter = h.child("namadokter").value.toString()
                        val alamat = h.child("tempat").value.toString() + ", " + h.child("alamat").value.toString()
                        val tanggal = h.child("tanggal").value.toString()
                        val jam = h.child("jam").value.toString()
                        val catatan = h.child("catatan").value.toString()
                        val status = h.child("status").value.toString()
                        val idjanji = h.key.toString()
                        root.agrName.text = namadokter
                        root.agrAddress.text = alamat
                        root.agrTanggal.text = tanggal
                        root.agrJam.text = jam + " WIB"
                        root.agrCatatan.text = catatan
                        root.agrAddress.contentDescription = "Alamat konsultasi, $alamat"
                        root.agrTanggal.contentDescription = "Hari dan tanggal, $tanggal"
                        root.agrJam.contentDescription = "Pukul, $jam WIB"
                        root.agrCatatan.contentDescription = "Catatan tambahan, $catatan"
                        if (teksBatalJanji != null) {
                            teksBatalJanji.visibility = View.VISIBLE
                            teksBatalJanji.setOnClickListener { popAlert(idjanji, root) }
                        }
                        shiftCardLayout(1)
                        when (status.toInt()) {
                            1 -> {
                                agrBtnStatus.text = "Diterima"
                                agrBtnStatus.setBackgroundResource(R.drawable.rounded_button_bot_green)
                                agrBtnStatus.contentDescription = "Status persetujuan diterima, lihat rincian pertemuan"
                                agrBtnStatus.setOnClickListener {
                                    val intent = Intent(root.context, RincianPersetujuan::class.java)
                                    intent.putExtra("idJanji", idjanji)
                                    root.context.startActivity(intent)
                                }
                                teksBatalJanji.visibility = View.GONE
                                shiftCardLayout(2)
                            }
                            2 -> {
                                agrBtnStatus.text = "Ditolak"
                                agrBtnStatus.setBackgroundResource(R.drawable.rounded_button_bot_red)
                                agrBtnStatus.contentDescription = "Status persetujuan ditolak, lihat alasan penolakan"
                                agrBtnStatus.setOnClickListener{
                                    val intent = Intent(root.context, RincianPersetujuan::class.java)
                                    intent.putExtra("idJanji", idjanji)
                                    root.context.startActivity(intent)
                                }
                                teksBatalJanji.visibility = View.GONE
                                shiftCardLayout(2)
                            }
                        }
                    }
                }
            }
        })

        ref.orderByChild("id_user").equalTo(auth.currentUser?.uid!!).addChildEventListener(object: ChildEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {
                val transaction = activity?.supportFragmentManager?.beginTransaction()
                transaction?.replace(R.id.framePersetujuanIsi, PersetujuanFragment())
                transaction?.commitAllowingStateLoss()
            }

        })
    }

    private fun getName() {
        ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.orderByKey().equalTo(auth.currentUser?.uid!!).addValueEventListener(object:
            ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    for (h in p0.children) {
                        val name = h.child("nama").value.toString()
                        root.agrHei.text = "Hai $name,"
                    }
                }
            }
        })
    }

    private fun popAlert(idjanji: String, view: View) {
        val dialog = AlertDialog.Builder(view.context).create()
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.pop_alert, null)
        dialog.setView(dialogView)
        dialog.setCancelable(true)
        dialogView.cardAlert.radius = 10F
        dialogView.alertText.text = "Apakah kamu yakin ingin membatalkan janji pertemuan?"
        dialogView.btnAccept.text = "Ya, Yakin"
        dialogView.btnAccept.setTextColor(Color.parseColor("#DB4437"))
        dialogView.btnCancel.setOnClickListener { dialog.dismiss() }
        dialogView.btnAccept.setOnClickListener {
            deleteJanji(idjanji)
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun deleteJanji(id: String) {
        ref = FirebaseDatabase.getInstance().getReference("janji")
        ref.child(id).removeValue()
    }

    private fun shiftCardLayout(jenis: Int) {
        when(jenis) {
            1 -> {
                root.guideline188.setGuidelinePercent(0.8386F)
                root.guideline189.setGuidelinePercent(0.1388F)
                root.guideline193.setGuidelinePercent(0.1768F)
                root.guideline194.setGuidelinePercent(0.3536F)
                root.guideline195.setGuidelinePercent(0.3902F)
                root.guideline196.setGuidelinePercent(0.4451F)
                root.guideline197.setGuidelinePercent(0.4817F)
                root.guideline198.setGuidelinePercent(0.5365F)
                root.guideline199.setGuidelinePercent(0.5792F)
                root.guideline200.setGuidelinePercent(0.8597F)
                root.guideline201.setGuidelinePercent(0.8780F)
            }
            else -> {
                root.guideline188.setGuidelinePercent(0.9322F)
                root.guideline189.setGuidelinePercent(0.1230F)
                root.guideline193.setGuidelinePercent(0.1526F)
                root.guideline194.setGuidelinePercent(0.3052F)
                root.guideline195.setGuidelinePercent(0.3368F)
                root.guideline196.setGuidelinePercent(0.3842F)
                root.guideline197.setGuidelinePercent(0.4157F)
                root.guideline198.setGuidelinePercent(0.4631F)
                root.guideline199.setGuidelinePercent(0.5F)
                root.guideline200.setGuidelinePercent(0.7605F)
                root.guideline201.setGuidelinePercent(0.8026F)
            }
        }
    }

}
