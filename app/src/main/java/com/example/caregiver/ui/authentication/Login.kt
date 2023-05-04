package com.example.caregiver.ui.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.caregiver.databinding.ActivityLoginBinding
import com.example.caregiver.ui.MainActivity
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        binding.redirectSignUp.setOnClickListener{
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }

        binding.btnLogin.setOnClickListener{

            val email = binding.email.text.toString()
            val password = binding.password.text.toString()

            fun isValidEmail(email: String): Boolean {
                val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
                return email.matches(emailRegex.toRegex())
            }

            if (email.isNotEmpty() || password.isNotEmpty()) {

                if (isValidEmail(email)){
                    firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener{
                        if(it.isSuccessful) {
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show()
                }

            } else {
                Toast.makeText(this, "All the fields are required", Toast.LENGTH_SHORT).show()
            }


        }
    }

    override fun onStart() {
        super.onStart()
        firebaseAuth = FirebaseAuth.getInstance()

        if(firebaseAuth.currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity((intent))
        }
    }
}