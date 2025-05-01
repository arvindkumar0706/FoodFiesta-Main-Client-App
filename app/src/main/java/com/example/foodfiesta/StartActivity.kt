package com.example.foodfiesta

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.foodfiesta.databinding.ActivityStartBinding

class StartActivity : AppCompatActivity() {
private val binding:ActivityStartBinding by lazy {
    ActivityStartBinding.inflate(layoutInflater)
}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.nextButton.setOnClickListener {
            val intent=Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }

    }
}