package com.example.birthdaygreet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    fun creatcard(view: View) {
        val name = nameinput.editableText.toString()
        Toast.makeText(this, "Opening new activity $name", Toast.LENGTH_LONG).show()
        val intent = Intent(this, BirthdayActivity::class.java)
        intent.putExtra(BirthdayActivity.NAME_EXTRA, name)
        startActivity(intent)
    }
}