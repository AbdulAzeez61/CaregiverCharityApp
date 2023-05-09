package com.example.caregiver.ui.account

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import com.bumptech.glide.Glide
import com.example.caregiver.R
import com.example.caregiver.databinding.ActivityAccountBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream


class Account : AppCompatActivity() {

    private lateinit var binding: ActivityAccountBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    val storageRef = FirebaseStorage.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        var user = firebaseAuth?.currentUser
        val userId = user?.uid
        val email = user?.email
        val profileImg = user?.photoUrl

        if (profileImg != null) {
            Glide.with(this).load(profileImg).into(binding.profileImg)
        }

        database = FirebaseDatabase.getInstance()


        val userRef = database.getReference("Users").child(userId!!)

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val firstName = dataSnapshot.child("firstName").getValue(String::class.java)
                val lastName = dataSnapshot.child("lastName").getValue(String::class.java)

                var fullName = firstName + " " + lastName
                binding.username.setText(fullName)

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
            }
        })

        binding.email.setText(email)

        binding.editProfilePic.setOnClickListener {
            // Create an AlertDialog to let the user choose between camera and gallery
            val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Add Photo!")
            builder.setItems(options) { dialog, item ->
                when (item) {
                    0 -> takePicture.launch()
                    1 -> pickImage.launch("image/*")
                    else -> dialog.dismiss()
                }
            }
            builder.show()
        }

        binding.updateEmail.setOnClickListener {
            val intent = Intent(this, UpdateEmail::class.java)
            startActivity(intent)
        }

        binding.updatePassword.setOnClickListener {
            val intent = Intent(this, UpdatePassword::class.java)
            startActivity(intent)
        }

        binding.updateProfileInfo.setOnClickListener {
            val intent = Intent(this, UpdateProfileInfo::class.java)
            startActivity(intent)
        }

        binding.devcAccount.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val inflater = layoutInflater
            val dialogLayout = inflater.inflate(R.layout.alert_input, null)
            val password = dialogLayout.findViewById<EditText>(R.id.password)

            with(builder) {
                setTitle("Enter Password")
                setPositiveButton("OK") { dialog, which ->
                    // Get the user's input from the EditText
                    val password = password.text.toString()
                    val credential =
                        email?.let { it1 -> EmailAuthProvider.getCredential(it1, password) }
                    if (credential != null) {
                        user?.reauthenticate(credential)
                            ?.addOnCompleteListener { task ->
                                if (task.isSuccessful) {

                                    user?.delete()?.addOnSuccessListener {
                                        // User account deleted successfully

                                        Toast.makeText(context, "Account deleted successfully", Toast.LENGTH_SHORT).show()
                                    }?.addOnFailureListener { exception ->
                                        // An error occurred while deleting the user's account
                                        Log.e(
                                            "DeleteAccount",
                                            "An error occurred: ${exception.message}"
                                        )
                                        Toast.makeText(context, "An error occurred while deleting your account", Toast.LENGTH_SHORT).show()
                                    }

                                    } else {
                                        // An error occurred while re-authenticating the user
                                    }
                                }
                            }
                    }
                    setNegativeButton("Cancel") { dialog, which ->
                        // Handle the cancel button
                    }
                    setView(dialogLayout)
                    show()
                }
            }


        }


        val pickImage =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                // Upload uri to Firebase storage
                uri?.let {
                    val imageRef = storageRef.child("images/${it.lastPathSegment}")
                    val uploadTask = imageRef.putFile(it)
                    uploadTask.addOnSuccessListener {
                        // Handle successful upload
                        // Handle successful upload
                        imageRef.downloadUrl.addOnSuccessListener { uri ->
                            val user = FirebaseAuth.getInstance().currentUser
                            val profileUpdates = UserProfileChangeRequest.Builder()
                                .setPhotoUri(uri)
                                .build()
                            user?.updateProfile(profileUpdates)
                                ?.addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(
                                            this,
                                            "profile img successfully setup",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        Glide.with(this).load(uri).into(binding.profileImg)
                                    }
                                }
                        }

                    }.addOnFailureListener {
                        // Handle failed upload
                        Toast.makeText(this, "profile img setupp failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }


        private val takePicture =
            registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
                // Upload bitmap to Firebase storage
                bitmap?.let {
                    val baos = ByteArrayOutputStream()
                    it.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                    val data = baos.toByteArray()

                    val imageRef = storageRef.child("images/${System.currentTimeMillis()}.jpg")
                    val uploadTask = imageRef.putBytes(data)
                    uploadTask.addOnSuccessListener {
                        // Handle successful upload
                        imageRef.downloadUrl.addOnSuccessListener { uri ->
                            // Store uri in user's profile
                            val user = FirebaseAuth.getInstance().currentUser
                            val profileUpdates = UserProfileChangeRequest.Builder()
                                .setPhotoUri(uri)
                                .build()
                            user?.updateProfile(profileUpdates)
                                ?.addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(
                                            this,
                                            "Profile image successfully set up",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        Glide.with(this).load(uri).into(binding.profileImg)
                                    } else {
                                        Toast.makeText(
                                            this,
                                            "Profile image setup failed",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                        }.addOnFailureListener { exception ->
                            // Handle failed download
                            Log.e("DeleteAccount", "An error occurred: ${exception.message}")
                            Toast.makeText(this, "Profile image setup failed", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }.addOnFailureListener { exception ->
                        // Handle failed upload
                        Log.e("MainActivity", "Upload failed", exception)
                        Toast.makeText(this, "Profile image setup failed", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }

    }