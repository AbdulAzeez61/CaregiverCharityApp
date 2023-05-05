package com.example.caregiver.ui.dashboard

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.caregiver.databinding.FragmentDashboardBinding
import com.example.caregiver.ui.account.Account
import com.example.caregiver.ui.entries.AllEntries
import com.example.caregiver.ui.entries.CreateEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlin.math.roundToInt


class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private lateinit var database: FirebaseDatabase
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        firebaseAuth = FirebaseAuth.getInstance()
        val user = firebaseAuth.currentUser
        val userId = user?.uid
        val email = user?.email
        val profileImg = user?.photoUrl

        if (profileImg != null) {
            Glide.with(this).load(profileImg).into(binding.profileImg)
        }

        var totalPaymentAmount = 0.0
        databaseReference = FirebaseDatabase.getInstance().getReference("Payment")

        databaseReference.orderByChild("userID").equalTo(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                    // Iterate over all payments with the given campaignID
                    for (itemSnapshot in snapshot.children) {
                        val payAmountString = itemSnapshot.child("payAmount").value.toString()
                        val payAmount = payAmountString.toDouble()

                        // Add the payAmount to the total payment amount
                        totalPaymentAmount += payAmount
                    }


                    binding.contribution.text =
                        "Rs.${totalPaymentAmount.roundToInt()}"
                }
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    Log.d("DashboardFragment", "Snapshot: $snapshot")
//                    // ...
//                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(ContentValues.TAG, "Failed to read value.", error.toException())
                }
            })
        var totalRaisedAmount = 0.0
        Log.d("DashboardFragment", "This is the raised amiunt $totalRaisedAmount")

        databaseReference.orderByChild("creatorID").equalTo(userId)
            ?.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                    // Iterate over all payments with the given campaignID
                    for (itemSnapshot in snapshot.children) {
                        val payAmountString = itemSnapshot.child("payAmount").value.toString()
                        val payAmount = payAmountString.toDouble()

                        // Add the payAmount to the total payment amount
                        totalRaisedAmount += payAmount
                        Log.d("DashboardFragment", "This is the raised amiunt inside $totalRaisedAmount")
                    }


                    binding.raised.text =
                        "Rs.${totalRaisedAmount.roundToInt()}"
                }
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    Log.d("DashboardFragment", "Snapshot 2: $snapshot")
//                    // ...
//                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(ContentValues.TAG, "Failed to read value.", error.toException())
                }
            })
        Log.d("DashboardFragment", "This is the after $totalRaisedAmount")

        database = FirebaseDatabase.getInstance()
        val userRef = database.getReference("Users").child(userId!!)

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val firstName = dataSnapshot.child("firstName").getValue(String::class.java)
                    val lastName = dataSnapshot.child("lastName").getValue(String::class.java)

                    val fullName = firstName + " " + lastName
                    binding.username.setText(fullName)
                    binding.email.setText(email)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
            }
        })

//        val textView: TextView = binding.textSlideshow
        dashboardViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.account.setOnClickListener {
            val intent = Intent(activity, Account::class.java)
            startActivity(intent)
        }

        binding.createProject.setOnClickListener {
            val intent = Intent(activity, CreateEntry::class.java)
            startActivity(intent)
        }


        binding.projects.setOnClickListener {
            val intent = Intent(activity, AllEntries::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}