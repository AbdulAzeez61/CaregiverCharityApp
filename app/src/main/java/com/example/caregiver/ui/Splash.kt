package com.example.caregiver.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.coroutines.delay
import android.view.WindowInsets
import androidx.lifecycle.lifecycleScope
import com.example.caregiver.databinding.ActivitySplashBinding
import com.example.caregiver.ui.authentication.Login
import kotlinx.coroutines.launch

class Splash : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private  var splash: Long = 5000


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        window.insetsController?.hide(WindowInsets.Type.statusBars())

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            delay(splash)
            val intent = Intent(this@Splash, Login::class.java)
            startActivity(intent)
            finish()
        }
    }
}