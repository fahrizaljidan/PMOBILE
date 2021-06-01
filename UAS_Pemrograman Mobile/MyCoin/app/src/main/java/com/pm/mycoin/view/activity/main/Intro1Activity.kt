package com.pm.mycoin.view.activity.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import com.pm.mycoin.R

class Intro1Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro1)
        val btnnext = findViewById<Button>(R.id.btnnext)

        btnnext?.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

    }
}