package com.example.caregiver.ui.account

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import com.example.caregiver.R
import com.example.caregiver.databinding.ActivityCompleteProfileBinding
import com.example.caregiver.ui.MainActivity
import com.example.caregiver.ui.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CompleteProfile : AppCompatActivity() {

    private lateinit var binding:ActivityCompleteProfileBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCompleteProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        /**
         * get firebase authentication user data
         */
        val user = firebaseAuth.currentUser
        val userId = user?.uid
        var countryCodePicker = binding.countryCode
        var phoneEditText = binding.phoneNo
        countryCodePicker.registerCarrierNumberEditText(phoneEditText)

        lateinit var gender:String
        lateinit var birthDay:String

        /**
         * selectio user options
         */
        val options = arrayOf("Organization", "Individual")

        val adapter = ArrayAdapter(
            this,
            R.layout.dropdown_menu_popup_item,
            options
        )

        val editTextFilledExposedDropdown = binding.userRoleSelect
        editTextFilledExposedDropdown.setAdapter(adapter)

        Log.d("CompleteProfile", editTextFilledExposedDropdown.toString())

        binding.birthDay.init(2000, 2, 10) { view, year, monthOfYear, dayOfMonth ->
            // Handle date change event
            birthDay = "$dayOfMonth/${monthOfYear + 1}/$year"
        }

        binding.genderSelect.setOnCheckedChangeListener { group, checkedId ->
            val radioButton = findViewById<RadioButton>(checkedId)
            gender = radioButton.text.toString()
        }

        /**
         * actions after user complete setup
         */
        binding.btnComplete.setOnClickListener{

            var firstName = binding.firstName.text.toString()
            var lastName = binding.lastName.text.toString()
            var phoneNo = binding.countryCode.getFullNumberWithPlus()
            var role = editTextFilledExposedDropdown.text.toString()

            var fullname = firstName + " "  + lastName

            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(fullname)
                .build()

            user?.updateProfile(profileUpdates)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                    }
                }


            databaseReference = FirebaseDatabase.getInstance().getReference("Users")

            var user = User(userId, phoneNo, firstName, lastName, birthDay, gender, role)

            /**
             * clear fields
             */
            if (userId != null) {
                databaseReference.child(userId).setValue(user).addOnCompleteListener{
                    if (it.isSuccessful){
                        binding.firstName.text?.clear()
                        binding.lastName.text?.clear()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Something wrong Check Your Connection.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}