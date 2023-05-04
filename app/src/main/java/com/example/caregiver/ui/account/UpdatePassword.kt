package com.example.caregiver.ui.account

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.caregiver.databinding.ActivityUpdatePasswordBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class UpdatePassword : AppCompatActivity() {
    private lateinit var binding: ActivityUpdatePasswordBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdatePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        val user = firebaseAuth?.currentUser
        val email = user?.email ?: ""

        binding.btnUpdate.setOnClickListener {

            val oldPassword = binding.oldPassword.text.toString()
            val newPassword = binding.newPassword.text.toString()

            val credential = EmailAuthProvider.getCredential(email, oldPassword)

            if (oldPassword.isNotEmpty() && newPassword.isNotEmpty()) {

                user?.reauthenticate(credential)?.addOnCompleteListener {
                    if (it.isSuccessful) {
                        user?.updatePassword(newPassword)?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // The password was updated successfully
                                Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show()
                            } else {
                                // The password update failed
                                Toast.makeText(this, "Password update failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(this, "Password update failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }
    }
}