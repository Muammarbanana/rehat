package com.example.rehat

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.room.Room
import com.example.rehat.fragmenthome.*
import com.example.rehat.pageradapter.PagerAdapter
import com.example.rehat.roomdb.RoomDB
import com.example.rehat.viewmodel.SharedViewModel
import com.example.rehat.viewmodel.ViewModelFactory
import com.google.android.material.tabs.TabLayout
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
                "editprofil" -> hideTitleProfil()
                "backtohomeprofil" -> showTitleProfil()
            }
        })

        getAllData()

        if (intent.extras != null) {
            Handler().postDelayed({
                val nomorTab = intent.getStringExtra("DataTabChat")
                tabsMain.setScrollPosition(nomorTab.toInt(), 0f, true)
                viewPager1.currentItem = nomorTab.toInt()
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

        imgBackProfil.setOnClickListener {
            showTitleProfil()
            supportFragmentManager.popBackStack()
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

    private fun hideTitleProfil() {
        homeTitle.visibility = View.GONE
        tombolChat.visibility = View.GONE
        imgBackProfil.visibility = View.VISIBLE
        teksEditProfil.visibility = View.VISIBLE
    }

    private fun showTitleProfil() {
        homeTitle.visibility = View.VISIBLE
        tombolChat.visibility = View.VISIBLE
        imgBackProfil.visibility = View.GONE
        teksEditProfil.visibility = View.GONE
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
        val datatitle = getSharedPreferences("DataTitle", Context.MODE_PRIVATE)
        homeTitle.visibility = datatitle.getInt("homeTitleState", 0)
        tombolChat.visibility = datatitle.getInt("tombolChatState", 0)
        imgBack.visibility = datatitle.getInt("imgBackState", View.GONE)
        teksPencarian.visibility = datatitle.getInt("teksPencarianState", View.GONE)

    }
}
