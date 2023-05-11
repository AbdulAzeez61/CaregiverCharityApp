package com.example.caregiver.ui.home

import android.os.Bundle
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import com.example.caregiver.ui.model.EntryData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.caregiver.databinding.FragmentHomeBinding
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
//
//                    val relativeTime = DateUtils.getRelativeTimeSpanString(dataClass?.createdTime ?: 0, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS)
                    if (dataClass != null) {
//                        dataClass.relativeTime = relativeTime.toString()
                        dataList.add(dataClass)
                    }

                }
                adapter?.searchProjects(dataList)
                adapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })


        val searchView = binding.search
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val filteredDataList = dataList.filter { entryData ->
                    entryData.entryTitle?.contains(newText ?: "", ignoreCase = true) == true
                }
                adapter?.searchProjects(filteredDataList)
                return true
            }
        })


        return root

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }
}