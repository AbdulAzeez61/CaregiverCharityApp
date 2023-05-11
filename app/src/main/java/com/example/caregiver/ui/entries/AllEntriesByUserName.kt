package com.example.caregiver.ui.entries

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.example.caregiver.R
import com.example.caregiver.databinding.ActivityAllEntriesByUserNameBinding
import com.example.caregiver.ui.model.EntryData
import com.example.caregiver.ui.model.User
import com.google.firebase.database.*


class AllEntriesByUserName : AppCompatActivity() {
    private lateinit var binding: ActivityAllEntriesByUserNameBinding
    private lateinit var databaseReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllEntriesByUserNameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val entryData = intent.getParcelableExtra<EntryData>("entrydata")
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(entryData?.userId ?: "defaultUserId" ?: "1")
//
        val profileImg = entryData?.profileImg


        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                val usernmae = user?.firstName + " " + user?.lastName
                val profileType = "Account Type: "  + user?.role
                val phoneNo = "Mobile: " +user?.phoneNo
                val birthday = "Birthday: " + user?.birthDay
                val gender = "Gender: " + user?.gender

                binding.username.setText(usernmae)
                binding.mobile.setText(phoneNo)
                binding.birthday.setText(birthday)
                binding.gender.setText(gender)
                binding.type.setText(profileType)


//                val profileImg = intent.getStringExtra("profileImg")
                if (profileImg != null) {
                    Glide.with(this@AllEntriesByUserName).load(profileImg).into(binding.profileImg)
                }

                binding.btnCall.setOnClickListener {
                    val phoneNumber = "tel:${user?.phoneNo}" // Use the phone number from the database
                    val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse(phoneNumber))
                    startActivity(dialIntent)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
            }
        })

    }
}