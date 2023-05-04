package com.example.caregiver.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.caregiver.R
import com.example.caregiver.databinding.FragmentHomeBinding
import com.example.caregiver.ui.model.Card

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

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
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val data = ArrayList<Card>()

        // This loop will create 20 Views containing
        // the image with the count of view
        for (i in 1..20) {
            data.add(Card(R.drawable.splash,"Item " + i))
        }

        // This will pass the ArrayList to our Adapter
        val adapter = CardAdapter(data)

        // Setting the Adapter with the recyclerview
        recyclerView.adapter = adapter


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}