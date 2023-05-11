package com.example.caregiver.ui.account

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.caregiver.databinding.ActivityUpdateEmailBinding
import com.google.firebase.auth.FirebaseAuth

class UpdateEmail : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: ActivityUpdateEmailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        /**
         * get firebase authentication user
         */
        val user = firebaseAuth?.currentUser
        val email = user?.email?: ""

        //set the current value
        binding.email.setText(email)
        /**
         * update user email
         */
        binding.btnUpdate.setOnClickListener {
            val email = binding.email.text.toString()

            fun isValidEmail(email: String): Boolean {
                val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
                return email.matches(emailRegex.toRegex())
            }

            if (email.isNotEmpty()) {
                if (isValidEmail(email)){
                     user?.updateEmail(email)?.addOnCompleteListener {
                         if (it.isSuccessful) {
                             Toast.makeText(this, "Email update successfully", Toast.LENGTH_SHORT).show()
                         } else {
                             Toast.makeText(this, "Email update failed", Toast.LENGTH_SHORT).show()
                         }
                     }
                } else {
                    Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Provide New Email", Toast.LENGTH_SHORT).show()
            }
        }

    }
}