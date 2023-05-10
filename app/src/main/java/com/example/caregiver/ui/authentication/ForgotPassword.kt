package com.example.caregiver.ui.authentication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.caregiver.R
import com.example.caregiver.databinding.ActivityAccountBinding
import com.example.caregiver.databinding.ActivityForgotPasswordBinding
import com.example.caregiver.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class ForgotPassword : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.sendButton.setOnClickListener {

            firebaseAuth.sendPasswordResetEmail(binding.email.text.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Password reset email sent successfully
                        Toast.makeText(
                            this,
                            "We send a password reset instruction to your email",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {
                        // Failed to send password reset email
                    }
                }
        }


    }
}