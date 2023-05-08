package com.example.caregiver.ui.payments

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.caregiver.databinding.ActivityReadPaymentsBinding
import com.example.caregiver.ui.model.CampPay
import com.example.caregiver.ui.model.EntryData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class AllPayments : AppCompatActivity() {
    private lateinit var binding: ActivityReadPaymentsBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var dataList: ArrayList<CampPay>
    private lateinit var adapter: MyAdapter
    var databaseReference: DatabaseReference? = null
    var eventListener: ValueEventListener? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReadPaymentsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val entryData = intent.getParcelableExtra<EntryData>("entrydata")

        val gridLayoutManager = GridLayoutManager(this, 1)
        binding.recyclerView.layoutManager = gridLayoutManager

        val builder = AlertDialog.Builder(this)
//        builder.setCancelable(false)
//        builder.setView(R.layout.progress_layout)

        val dialog = builder.create()
        dialog.show()

        firebaseAuth = FirebaseAuth.getInstance()
        var user = firebaseAuth.currentUser
        var userId = user?.uid

        dataList = ArrayList()
        adapter = MyAdapter(this, dataList)
        binding.recyclerView.adapter = adapter

        databaseReference = FirebaseDatabase.getInstance().getReference("Payment")

        val query = databaseReference?.orderByChild("userID")?.equalTo(userId)


        dialog.show()

        eventListener = query?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dataList.clear()

                for (itemSnapshot in snapshot.children) {
                    val dataClass = itemSnapshot.getValue(CampPay::class.java)

                    if (dataClass != null) {
                        dataList.add(dataClass)
                    }
                }

                adapter.notifyDataSetChanged()
                dialog.dismiss()
            }

            override fun onCancelled(error: DatabaseError) {
                dialog.dismiss()
            }


        })
    }
}