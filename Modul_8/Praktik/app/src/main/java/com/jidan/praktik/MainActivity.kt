package com.jidan.praktik

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val view_pager = findViewById<ViewPager>(R.id.view_pager)
        view_pager?.adapter = ViewPagerAdapter(
                    this, supportFragmentManager)

        val tabs = findViewById<TabLayout>(R.id.tabs)
        tabs?.setupWithViewPager(view_pager)
    }
}