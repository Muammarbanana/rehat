package com.example.rehat

import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.material.tabs.TabLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.room.Room
import com.example.rehat.fragmenthome.*
import com.example.rehat.pageradapter.PagerAdapter
import com.example.rehat.roomdb.RoomDB
import com.example.rehat.viewmodel.SharedViewModel
import com.example.rehat.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.activity_home.*
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

        viewModel.listenMateri().observe(this, Observer{
            getAllData()
        })

        if (intent.extras != null) {
            val nomorTab = intent.getStringExtra("DataTabChat")
            tabsMain.getTabAt(nomorTab.toInt())?.select()
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
    }

    override fun onResume() {
        super.onResume()
        getAllData()
        setupTabIcons()
    }

    fun getAllData(){
        var pages: ArrayList<Fragment>
        roomDB?.materiDao()?.getAll()?.observe(this, Observer{
            if (it?.isNotEmpty()!!) {
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
}
