package com.example.foodfiesta

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.foodfiesta.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var navController=findNavController(R.id.fragmentContainerView)
        val bottomnav: BottomNavigationView = findViewById(R.id.bottomNav)
        bottomnav.setupWithNavController(navController)

        binding.notificationIcon.setOnClickListener {
            val bottomSheetDialog=Notification_bottom_Fragment()
            bottomSheetDialog.show(supportFragmentManager,"Test")
        }


    }
}