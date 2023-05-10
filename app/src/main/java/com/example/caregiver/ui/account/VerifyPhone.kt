package com.example.caregiver.ui.account

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.caregiver.R
import com.example.caregiver.databinding.ActivityAccountBinding
import com.example.caregiver.databinding.ActivityVerifyPhoneBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

class VerifyPhone : AppCompatActivity() {

    private lateinit var binding: ActivityVerifyPhoneBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVerifyPhoneBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setContentView(R.layout.activity_verify_phone)

        firebaseAuth = FirebaseAuth.getInstance()
        var user = firebaseAuth?.currentUser
        val userId = user?.uid
        val email = user?.email
        val profileImg = user?.photoUrl

        binding.verifyButton.setOnClickListener {

        }

    }

}
