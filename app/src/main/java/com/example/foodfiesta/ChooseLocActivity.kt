package com.example.foodfiesta

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.foodfiesta.databinding.ActivityChooseLocBinding

class ChooseLocActivity : AppCompatActivity() {
    private val binding:ActivityChooseLocBinding by lazy {
        ActivityChooseLocBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        val locationList = arrayOf("Jaipur","Odisha","Mumbai")
        val adapter=ArrayAdapter(this,android.R.layout.simple_list_item_1,locationList)
        val autoCompleteTextView=binding.listoflocation
        autoCompleteTextView.setAdapter(adapter)

    }
}