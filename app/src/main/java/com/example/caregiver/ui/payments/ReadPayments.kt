package com.example.caregiver.ui.payments

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.caregiver.databinding.ActivityReadPaymentsBinding
import com.example.caregiver.ui.model.CampPay
import com.example.caregiver.ui.model.EntryData
import com.google.firebase.database.*

class ReadPayments : AppCompatActivity() {
    private lateinit var binding: ActivityReadPaymentsBinding
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

        dataList = ArrayList()
        adapter = MyAdapter(this, dataList)
        binding.recyclerView.adapter = adapter

        databaseReference = FirebaseDatabase.getInstance().getReference("Payment")

        val campid = entryData?.entryKey //hardcode
        val query = databaseReference?.orderByChild("cid")?.equalTo(campid)


        dialog.show()
        val paymView = binding.payView

//        eventListener= databaseReference!!.addValueEventListener(object: ValueEventListener{
//            override fun onDataChange(snapshot: DataSnapshot) {
//                dataList.clear()
//
//                for(itemSnapshot in snapshot.children){
//                    val dataClass = itemSnapshot.getValue(CampPay::class.java)
//
//                    if(dataClass!=null){
//                        dataList.add(dataClass)
//                    }
//
//                }
//
//                adapter.notifyDataSetChanged()
//                dialog.dismiss()
//            }

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

        // Query the database for all payments with the given campaignID
        val campaignID = entryData?.entryKey //hardcode
        var totalPaymentAmount = 0.0

        databaseReference?.orderByChild("cid")?.equalTo(campaignID)
            ?.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Iterate over all payments with the given campaignID
                    for (itemSnapshot in snapshot.children) {
                        val payAmountString = itemSnapshot.child("payAmount").value.toString()
                        val payAmount = payAmountString.toDouble()

                        // Add the payAmount to the total payment amount
                        totalPaymentAmount += payAmount
                    }


                    paymView.text =
                        "Total payment amount for campaign $campaignID: $totalPaymentAmount"

                    // Display the total payment amount for the given campaignID
                    Log.d(
                        ContentValues.TAG,
                        "Total payment amount for campaign $campaignID: $totalPaymentAmount"
                    )
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(ContentValues.TAG, "Failed to read value.", error.toException())
                }
            })


    }
}

