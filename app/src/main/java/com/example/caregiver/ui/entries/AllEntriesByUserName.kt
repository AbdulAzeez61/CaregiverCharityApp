package com.example.caregiver.ui.entries

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.caregiver.databinding.ActivityAllEntriesByUserNameBinding
import com.example.caregiver.ui.model.EntryData

class AllEntriesByUserName : AppCompatActivity() {
    private lateinit var binding: ActivityAllEntriesByUserNameBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllEntriesByUserNameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val entryData = intent.getParcelableExtra<EntryData>("entrydata")

        binding.username.text = entryData!!.userId
    }
}