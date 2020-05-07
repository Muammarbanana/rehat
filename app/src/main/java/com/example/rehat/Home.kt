package com.example.rehat

import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.util.Log
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

        viewModel.selected.observeForever(android.arch.lifecycle.Observer {
            if (it.equals("go to tab 1")) {
                tabsMain.getTabAt(0)?.select()
            }
        })

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
