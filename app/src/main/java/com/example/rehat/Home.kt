package com.example.rehat

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.room.Room
import com.example.rehat.fragmenthome.*
import com.example.rehat.model.Notifikasi
import com.example.rehat.model.NotifikasiMateri
import com.example.rehat.model.SubMateriKomplit
import com.example.rehat.pageradapter.PagerAdapter
import com.example.rehat.roomdb.RoomDB
import com.example.rehat.viewmodel.SharedViewModel
import com.example.rehat.viewmodel.ViewModelFactory
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_konsultasi.*
import kotlinx.android.synthetic.main.pop_alert.view.*
import java.util.*


class Home : AppCompatActivity() {

    private var roomDB: RoomDB? = null
    private lateinit var viewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        roomDB = Room.databaseBuilder(this, RoomDB::class.java, "materiDB").allowMainThreadQueries().build()

        viewModel = ViewModelProviders.of(this,
            ViewModelFactory(roomDB!!.materiDao())
        )[SharedViewModel::class.java]

        viewModel.selected.observeForever(androidx.lifecycle.Observer {
            when (it) {
                "go to tab 1" -> tabsMain.getTabAt(0)?.select()
                "searching" -> hideTitle()
                "backtohome" -> showTitle()
            }
        })

        viewModel.removed.observe(this, Observer {
            val tr = supportFragmentManager.beginTransaction()
            tr.replace(R.id.savedPageConst, HalamanTersimpanIsiFragment())
            tr.commit()
        })

        getAllData()

        if (intent.extras != null) {
            Handler().postDelayed({
                val nomorTab = intent.getStringExtra("DataTabChat")
                if (nomorTab == "2") {
                    tabsMain.setScrollPosition(nomorTab.toInt(), 0f, true)
                    viewPager1.currentItem = nomorTab.toInt()
                } else {
                    tabsMain.setScrollPosition(2, 0f, true)
                    viewPager1.currentItem = 2
                    viewModel.selectedTab("ke halaman persetujuan")
                }
            },100)
        }

        tombolChat.setOnClickListener {
            startActivity(Intent(this, KonsultasiOnline::class.java))
        }

        imgBack.setOnClickListener {
            showTitle()
            val tr = supportFragmentManager.beginTransaction()
            tr.replace(R.id.edukasiConst, EdukasiFragment())
            tr.commit()
        }

        checkChatKonsul()
        removeExpiredRequest()

        // ganti title saat tab dipilih
        tabsMain.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab?.position) {
                    0 -> homeTitle.text = "Edukasi"
                    1 -> homeTitle.text = "Materi Tersimpan"
                    2 -> homeTitle.text = "Konsultasi"
                    3 -> homeTitle.text = "Notifikasi"
                    else -> homeTitle.text = "Profile"
                }
            }
        })
    }

    // set ikon pada tab
    fun setupTabIcons() {
        tabsMain.getTabAt(0)?.setIcon(R.drawable.tab_selector_edukasi)?.contentDescription = "Edukasi"
        tabsMain.getTabAt(1)?.setIcon(R.drawable.tab_selector_halaman_tersimpan)?.contentDescription = "Materi Tersimpan"
        tabsMain.getTabAt(2)?.setIcon(R.drawable.tab_selector_konsultasi)?.contentDescription = "Konsultasi"
        tabsMain.getTabAt(3)?.setIcon(R.drawable.tab_selector_notifikasi)?.contentDescription = "Notifikasi"
        tabsMain.getTabAt(4)?.setIcon(R.drawable.tab_selector_profile)?.contentDescription = "Profil"
        val iduser = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("notifikasi")
        var jumlahnotif = 0
        ref.orderByChild("iduser").equalTo(iduser).addChildEventListener(object: ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val jum = p0.child("statusbaca").value.toString()
                if (jum == "0") {
                    jumlahnotif += 1
                }
                if (jumlahnotif != 0) {
                    val notifbadges = tabsMain.getTabAt(3)?.orCreateBadge
                    notifbadges?.backgroundColor = Color.parseColor("#FB3939")
                    notifbadges?.isVisible = true
                    notifbadges?.number = jumlahnotif
                    tabsMain.getTabAt(3)?.contentDescription = "Terdapat $jumlahnotif notifikasi baru belum dilihat"
                } else {
                    tabsMain.getTabAt(3)?.setIcon(R.drawable.tab_selector_notifikasi)?.contentDescription = "Notifikasi"
                }
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val jum = p0.child("statusbaca").value.toString()
                if (jum == "0") {
                    jumlahnotif += 1
                }
                if (jumlahnotif != 0) {
                    val notifbadges = tabsMain.getTabAt(3)?.orCreateBadge
                    notifbadges?.backgroundColor = Color.parseColor("#FB3939")
                    notifbadges?.isVisible = true
                    notifbadges?.number = jumlahnotif
                    tabsMain.getTabAt(3)?.contentDescription = "Terdapat $jumlahnotif notifikasi baru belum dilihat"
                } else {
                    tabsMain.getTabAt(3)?.contentDescription = "Notifikasi"
                }
            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }

        })
    }

    fun getAllData(){
        var pages: ArrayList<Fragment>
        roomDB?.materiDao()?.getAll()?.observe(this, Observer{
            if (it?.size != 0) {
                pages = arrayListOf(
                    EdukasiFragment(),
                    HalamanTersimpanIsiFragment(),
                    KonsultasiFragment(),
                    NotifikasiFragment(),
                    ProfileFragment()
                )
            } else {
                pages = arrayListOf(
                    EdukasiFragment(),
                    HalamanTersimpanFragment(),
                    KonsultasiFragment(),
                    NotifikasiFragment(),
                    ProfileFragment())
            }
            viewPager1.adapter =
                PagerAdapter(
                    supportFragmentManager,
                    pages
                )
            tabsMain.setupWithViewPager(viewPager1)
            setupTabIcons()
        })
    }

    private fun checkChatKonsul() {
        val ref = FirebaseDatabase.getInstance().getReference("messages")
        ref.orderByKey().equalTo(FirebaseAuth.getInstance().uid).addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    p0.children.forEach { p1 ->
                        p1.children.forEach {
                            val id = it.key.toString()
                            tombolChat.setOnClickListener {
                                val intent = Intent(this@Home, ChatKonsultasi::class.java)
                                intent.putExtra("Id", id)
                                startActivity(intent)
                            }
                            tombolChat.setImageResource(R.drawable.ic_pesan_konsultasi)
                            val ref1 = FirebaseDatabase.getInstance().getReference("konselor")
                            ref1.orderByKey().equalTo(id).addListenerForSingleValueEvent(object: ValueEventListener{
                                override fun onCancelled(p0: DatabaseError){
                                }

                                override fun onDataChange(p0: DataSnapshot) {
                                    p0.children.forEach {
                                        val nama = it.child("nama_konselor").value.toString()
                                        tombolChat.contentDescription = "Terdapat pesan masuk dari $nama"
                                    }
                                }
                            })
                        }
                    }
                } else {
                    tombolChat.setOnClickListener {
                        val intent = Intent(this@Home, KonsultasiOnline::class.java)
                        startActivity(intent)
                    }
                    tombolChat.setImageResource(R.drawable.ic_chat)
                    tombolChat.contentDescription = "pesan konsultasi online"
                }
            }
        })
    }

    private fun hideTitle() {
        homeTitle.visibility = View.GONE
        tombolChat.visibility = View.GONE
        imgBack.visibility = View.VISIBLE
        teksPencarian.visibility = View.VISIBLE
    }

    private fun showTitle() {
        homeTitle.visibility = View.VISIBLE
        tombolChat.visibility = View.VISIBLE
        imgBack.visibility = View.GONE
        teksPencarian.visibility = View.GONE
    }

    private fun setSharedPref(homeTitle: Int, tombolChat: Int, imgBack: Int, teksPencarian: Int) {
        val datatitle = getSharedPreferences("DataTitle", Context.MODE_PRIVATE)
        val titledata = datatitle.edit()
        titledata.putInt("homeTitleState", homeTitle)
        titledata.putInt("tombolChatState", tombolChat)
        titledata.putInt("imgBackState", imgBack)
        titledata.putInt("teksPencarianState", teksPencarian)
        titledata.apply()
    }

    override fun onPause() {
        super.onPause()
        setSharedPref(homeTitle.visibility, tombolChat.visibility, imgBack.visibility, teksPencarian.visibility)
    }

    override fun onResume() {
        super.onResume()
        checkChatKonsul()
        setupTabIcons()
        val datatitle = getSharedPreferences("DataTitle", Context.MODE_PRIVATE)
        homeTitle.visibility = datatitle.getInt("homeTitleState", 0)
        tombolChat.visibility = datatitle.getInt("tombolChatState", 0)
        imgBack.visibility = datatitle.getInt("imgBackState", View.GONE)
        teksPencarian.visibility = datatitle.getInt("teksPencarianState", View.GONE)
    }

    private fun removeExpiredRequest() {
        val ref = FirebaseDatabase.getInstance().getReference("janji")
        ref.orderByChild("status").equalTo(1.toDouble()).addValueEventListener(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    for (h in p0.children) {
                        val idjanji = h.key.toString()
                        val timestamp = h.child("timestamp").value
                        if (timestamp != null) {
                            val elapsedtime = System.currentTimeMillis() - timestamp.toString().toLong()
                            if (elapsedtime > 86400000) {
                                val rev = FirebaseDatabase.getInstance().getReference("janji")
                                rev.child(idjanji).removeValue()
                            }
                        }
                    }
                }
            }

        })
    }
}
