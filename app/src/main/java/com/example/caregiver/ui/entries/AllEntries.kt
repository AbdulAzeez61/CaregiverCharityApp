package com.example.caregiver.ui.entries

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.caregiver.databinding.ActivityAllEntriesBinding
import com.example.caregiver.ui.model.EntryData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class AllEntries : AppCompatActivity() {
    var databaseReference: DatabaseReference? = null
    var eventListener: ValueEventListener? = null
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var dataList: ArrayList<EntryData>
    private lateinit var adapter: MyEntryAdapter
    private lateinit var binding: ActivityAllEntriesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllEntriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val gridLayoutManager = GridLayoutManager(this@AllEntries, 1)
        binding.recyclerView.layoutManager = gridLayoutManager
        val builder = AlertDialog.Builder(this@AllEntries)
        builder.setCancelable(false)

        val dialog = builder.create()
        dialog.show()

        //passing values from dataclass into arraylist
        dataList = ArrayList()
        adapter = MyEntryAdapter(this@AllEntries, dataList)
        binding.recyclerView.adapter = adapter

        //firebase reference
        databaseReference =
            FirebaseDatabase.getInstance()
                .getReference("Entry Info")

        dialog.show()

        //getting current user
        firebaseAuth = FirebaseAuth.getInstance()
        var user = firebaseAuth.currentUser
        val desiredUserID = user?.uid

        //adding new recycler view items through dataSnapShot
        eventListener = databaseReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dataList.clear()
                for (itemSnapshot in snapshot.children) {
                    val dataClass = itemSnapshot.getValue(EntryData::class.java)
                    if (dataClass != null && dataClass.userId == desiredUserID) {
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