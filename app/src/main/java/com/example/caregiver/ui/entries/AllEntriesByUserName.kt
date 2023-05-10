package com.example.caregiver.ui.entries

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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

            binding.username.text = entryData?.userId


        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(entryData?.userId ?: "defaultUserId" ?: "01")

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                val usernmae = user?.firstName + " " + user?.lastName
                val phoneNo = user?.phoneNo
                val birthday = user?.birthDay
                val gender = user?.gender
                binding.username.setText(usernmae)
                binding.mobile.setText(phoneNo)
                binding.birthday.setText(birthday)
                binding.birthday.setText(gender)

//
//                when (user?.gender) {
//                    "Male" -> binding.genderSelect.check(R.id.male)
//                    "Female" -> binding.genderSelect.check(R.id.female)
//                    "Not Preferred" -> binding.genderSelect.check(R.id.invalid)
//                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
            }
        })

    }
}