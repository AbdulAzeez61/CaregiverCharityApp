package com.example.caregiver.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.caregiver.databinding.FragmentHomeBinding
import com.example.caregiver.ui.model.EntryData
import com.google.firebase.database.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var databaseReference: DatabaseReference

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root


        val recyclerView =  binding.recyclerview
        databaseReference = FirebaseDatabase.getInstance().getReference("Entry Info")
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

//        val gridLayoutManager = GridLayoutManager(context, 1)
//        binding.recyclerview.layoutManager = gridLayoutManager
////        recyclerView.layoutManager = LinearLayoutManager(requireContext())
//
////        val data = ArrayList<Card>()
//
//        val data = ArrayList<Card>()
//        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                for (snapshot in dataSnapshot.children) {
//                    val title = snapshot.child("entryTitle").getValue(String::class.java)
//                    val description = snapshot.child("entryDescription").getValue(String::class.java)
//                    val genericTypeIndicator = object : GenericTypeIndicator<List<String>>() {}
//                    val images = snapshot.child("entryImages").getValue(genericTypeIndicator)
//                    val image = images?.get(0)
//                    if (title != null && description != null && image != null) {
//                        data.add(Card(image, title, description))
//                        Log.d("Data", "Added card with title: $title, description: $description, and image: $image")
//                    }
//                }
//                // Pass the cardList to your RecyclerView adapter here
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                // Handle database error
//            }
//        })


        // This loop will create 20 Views containing
        // the image with the count of view
//        for (i in 1..20) {
//            data.add(Card(R.drawable.splash,"Item " + i))
//        }

        // This will pass the ArrayList to our Adapter


//        val adapter = CardAdapter(this, data)
//
//        // Setting the Adapter with the recyclerview
//        recyclerView.adapter = adapterval builder = AlertDialog.Builder(this@AllEntries)
        var dataList = ArrayList<EntryData>()

        var adapter = context?.let { CardAdapter(it, dataList) }
        binding.recyclerview.adapter = adapter
        databaseReference =
            FirebaseDatabase.getInstance()
                .getReference("Entry Info")

        databaseReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dataList.clear()
                for (itemSnapshot in snapshot.children) {
                    val dataClass = itemSnapshot.getValue(EntryData::class.java)
                    if (dataClass != null) {
                        dataList.add(dataClass)
                    }
                }
                adapter?.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        return root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }
}