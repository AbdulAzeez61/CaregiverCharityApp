package com.example.caregiver.ui.account

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import android.widget.Toast
import com.example.caregiver.R
import com.example.caregiver.databinding.ActivityAccountBinding
import com.example.caregiver.databinding.ActivityUpdateProfileInfoBinding
import com.example.caregiver.ui.MainActivity
import com.example.caregiver.ui.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class UpdateProfileInfo : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateProfileInfoBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateProfileInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /**
         * get firebase authentication user
         */
        firebaseAuth = FirebaseAuth.getInstance()
        var user = firebaseAuth?.currentUser
        val userId = user?.uid

        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId!!)

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            /**
             * get the current profile information
             */
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)

                binding.firstName.setText(user?.firstName)
                binding.lastName.setText(user?.lastName)
                binding.phoneNo.setText(user?.phoneNo)

                val birthDay = user?.birthDay?.split("/")?.map { it.toInt() }
                if (birthDay != null && birthDay.size == 3) {
                    binding.birthDay.updateDate(birthDay[2], birthDay[0] - 1, birthDay[1])
                }

                /**
                 * gender selections
                 */
                when (user?.gender) {
                    "Male" -> binding.genderSelect.check(R.id.male)
                    "Female" -> binding.genderSelect.check(R.id.female)
                    "Not Preferred" -> binding.genderSelect.check(R.id.invalid)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })

        var countryCodePicker = binding.countryCode
        var phoneEditText = binding.phoneNo
        countryCodePicker.registerCarrierNumberEditText(phoneEditText)

        lateinit var gender:String
        lateinit var birthDay:String


        binding.birthDay.init(2000, 2, 10) { view, year, monthOfYear, dayOfMonth ->
            // Handle date change event
            birthDay = "$dayOfMonth/${monthOfYear + 1}/$year"
        }

        binding.genderSelect.setOnCheckedChangeListener { group, checkedId ->
            val radioButton = findViewById<RadioButton>(checkedId)
            gender = radioButton.text.toString()
        }


        /**
         * update the changes
         */
        binding.btnUpdate.setOnClickListener {
            val firstName = binding.firstName.text.toString()
            val lastName = binding.lastName.text.toString()
            val phoneNo = binding.countryCode.getFullNumberWithPlus()

            val updates = hashMapOf<String, Any>(
                "firstName" to firstName,
                "lastName" to lastName,
                "phoneNo" to phoneNo,
                "gender" to gender,
                "birthDay" to birthDay
            )


            databaseReference.updateChildren(updates).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Update successful", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }


}