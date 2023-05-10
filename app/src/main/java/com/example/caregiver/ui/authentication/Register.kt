package com.example.caregiver.ui.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.caregiver.ui.account.CompleteProfile
import com.example.caregiver.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth

class Register : AppCompatActivity() {

    private lateinit var binding:ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.btnSignup.setOnClickListener{
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            val rePassword = binding.rePassword.text.toString()

            binding.redirectSignIn.setOnClickListener{
                val intent = Intent(this, Login::class.java)
                startActivity(intent)
            }

            if(email.isEmpty()) {
                binding.email.setError("Please enter your email")
            }

            if (password.isEmpty()){
                binding.password.setError("Please enter your password")
            }

            if (rePassword.isEmpty()){
                binding.rePassword.setError("Please retype your password")
            }


            if ( email.isNotEmpty() || password.isNotEmpty() || rePassword.isNotEmpty() ) {
                if(password == rePassword) {

                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{
                        if(it.isSuccessful) {
                              val intent = Intent(this, CompleteProfile::class.java)
                              startActivity(intent)
                        } else {
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Password is not matching!!", Toast.LENGTH_SHORT).show()
                    binding.rePassword.setError("Password are not matching")
                }
            } else {
                Toast.makeText(this, "All the fields are required!!", Toast.LENGTH_SHORT).show()
            }
        }
    }

}