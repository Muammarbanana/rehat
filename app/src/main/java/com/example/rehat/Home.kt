package com.example.rehat

import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_home.*
import java.util.*

class Home : AppCompatActivity() {

    private lateinit var viewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        viewPager1.adapter =
            PagerAdapter(supportFragmentManager)
        tabsMain.setupWithViewPager(viewPager1)
        setupTabIcons()

        viewModel = ViewModelProviders.of(this)[SharedViewModel::class.java]

        viewModel.selected.observeForever(androidx.lifecycle.Observer {
            if (it.equals("go to tab 1")) {
                tabsMain.getTabAt(0)?.select()
            }
        })

        if (intent.extras != null) {
            val nomorTab = intent.getStringExtra("DataTabChat")
            tabsMain.getTabAt(nomorTab.toInt())?.select()
        }

        tombolChat.setOnClickListener {
            startActivity(Intent(this, KonsultasiOnline::class.java))
        }

        // ganti title saat tab dipilih
        tabsMain.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab?.position) {
                    0 -> homeTitle.text = "Edukasi"
                    1 -> homeTitle.text = "Halaman Tersimpan"
                    2 -> homeTitle.text = "Konsultasi"
                    3 -> homeTitle.text = "Notifikasi"
                    else -> homeTitle.text = "Profile"
                }
            }
        })
    }

    // set ikon pada tab
    fun setupTabIcons() {
        tabsMain.getTabAt(0)?.setIcon(R.drawable.tab_selector_edukasi)
        tabsMain.getTabAt(1)?.setIcon(R.drawable.tab_selector_halaman_tersimpan)
        tabsMain.getTabAt(2)?.setIcon(R.drawable.tab_selector_konsultasi)
        tabsMain.getTabAt(3)?.setIcon(R.drawable.tab_selector_notifikasi)
        tabsMain.getTabAt(4)?.setIcon(R.drawable.tab_selector_profile)
    }
}
