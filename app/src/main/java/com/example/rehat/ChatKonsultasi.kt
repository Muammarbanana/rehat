package com.example.rehat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.example.rehat.model.ChatMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_konsultasi.*
import kotlinx.android.synthetic.main.chat_from.view.*
import kotlinx.android.synthetic.main.chat_from.view.chatime
import kotlinx.android.synthetic.main.chat_from.view.teksChat
import kotlinx.android.synthetic.main.chat_to.view.*
import kotlinx.android.synthetic.main.date_header.view.*
import java.text.SimpleDateFormat
import java.util.*

class ChatKonsultasi : AppCompatActivity() {

    val adapter = GroupAdapter<GroupieViewHolder>()
    private val SPEECH_REQUEST_CODE = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_konsultasi)

        getConselorName(intent.getStringExtra("Id"))

        listenForMessages()

        val layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        rvChat.layoutManager = layoutManager
        rvChat.adapter = adapter

        editTextMessage.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (editTextMessage.text.toString() == ""){
                    imgMic.visibility = View.VISIBLE
                    imgSend.visibility = View.GONE
                } else {
                    imgMic.visibility = View.GONE
                    imgSend.visibility = View.VISIBLE
                }
            }
        })

        imgMic.setOnClickListener { getVoice() }

        imgSend.setOnClickListener{
            val message = editTextMessage.text.toString()
            if (message.isNotEmpty()) {
                performSendMessage(message)
            }
        }

        teksSelesai.setOnClickListener {
            akhiriChat()
        }
    }

    fun getBack(view: View) {
        finish()
    }

    private fun getConselorName(id: String) {
        val ref = FirebaseDatabase.getInstance().getReference("konselor")
        ref.orderByKey().equalTo(id).addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    p0.children.forEach {
                        val nama = it.child("nama_konselor").value.toString()
                        chatNamaKonselor.text = nama
                    }
                }
            }

        })
    }

    private fun performSendMessage(message: String) {
        val fromId = FirebaseAuth.getInstance().uid
        val toId = intent.getStringExtra("Id")
        val timestamp = System.currentTimeMillis()
        val ref = FirebaseDatabase.getInstance().getReference("messages/$fromId/$toId")
        val toref = FirebaseDatabase.getInstance().getReference("messages/$toId/$fromId")
        val latestFromRef = FirebaseDatabase.getInstance().getReference("latest_messages/$fromId/$toId")
        val latestToRef = FirebaseDatabase.getInstance().getReference("latest_messages/$toId/$fromId")
        val chatmessage = ChatMessage(fromId!!, toId, message, timestamp)
        ref.push().setValue(chatmessage).addOnSuccessListener {
            editTextMessage.text.clear()
            rvChat.scrollToPosition(adapter.itemCount - 1)
        }
        toref.push().setValue(chatmessage)
        latestFromRef.setValue(chatmessage)
        latestToRef.setValue(chatmessage)
    }

    private fun listenForMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val toId = intent.getStringExtra("Id")
        var date = ""
        val ref = FirebaseDatabase.getInstance().getReference("messages/$fromId/$toId")
        ref.addChildEventListener(object: ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatmessage = p0.getValue(ChatMessage::class.java)

                if (chatmessage != null) {
                    if (date != convertToDate(Date(chatmessage.timestamp))) {
                        adapter.add(DateItem(convertToDate(Date(chatmessage.timestamp))))
                    }
                    if (chatmessage.fromId == FirebaseAuth.getInstance().uid) {
                        adapter.add(ChatToItem(chatmessage.message, converToHours(chatmessage.timestamp)))
                    } else {
                        adapter.add(ChatFromItem(chatmessage.message, converToHours(chatmessage.timestamp)))
                    }
                    date = convertToDate(Date(chatmessage.timestamp))
                }
            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }

        })

    }

    private fun converToHours(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH.mm", Locale.getDefault())
        val time = Date(timestamp)
        return sdf.format(time)
    }

    private fun convertToDate(timestamp: Date): String {
        var datevalue: String
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val sdf2 = SimpleDateFormat("dd", Locale.getDefault())
        val currentdate = Calendar.getInstance().time
        if (sdf.format(currentdate) == sdf.format(timestamp)) {
            datevalue = "Hari Ini"
        } else if (sdf2.format(timestamp).toInt() == sdf2.format(currentdate).toInt() - 1) {
            datevalue = "Kemarin"
        } else {
            datevalue = sdf.format(timestamp)
        }
        return datevalue
    }

    private fun akhiriChat() {
        val fromId = FirebaseAuth.getInstance().uid
        val toId = intent.getStringExtra("Id")
        val fromRef = FirebaseDatabase.getInstance().getReference("messages/$fromId/$toId")
        val toRef = FirebaseDatabase.getInstance().getReference("messages/$toId/$fromId")
        val latestToRef = FirebaseDatabase.getInstance().getReference("latest_messages/$toId/$fromId")
        val latestFromRef = FirebaseDatabase.getInstance().getReference("latest_messages/$fromId/$toId")
        fromRef.removeValue()
        toRef.removeValue()
        latestToRef.removeValue()
        latestFromRef.removeValue()
        finish()
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
            editTextMessage.setText(spokenText)

        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}

class ChatFromItem(val text: String, val time: String): Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.chat_from
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.teksChat.text = text
        viewHolder.itemView.chatime.text = time
    }

}

class ChatToItem(val text: String, val time: String): Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.chat_to
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.teksChatTo.text = text
        viewHolder.itemView.chatimeTo.text = time
    }

}

class DateItem(val text: String): Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.date_header
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.teksTanggal.text = text
    }

}
