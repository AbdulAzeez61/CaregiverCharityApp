package com.example.caregiver.ui.entries

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.caregiver.R
import com.example.caregiver.ui.model.EntryData
import com.example.caregiver.ui.payments.ReadPayments
import com.google.firebase.database.FirebaseDatabase

class MyEntryAdapter(private val context: Context, private var dataList: List<EntryData>) :
    RecyclerView.Adapter<MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_item_entries, parent, false)
        return MyViewHolder(view)
    }

    //loading details in to recycler view item
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if (dataList[position].entryImages.isNotEmpty()) {
            Glide.with(context).load(dataList[position].entryImages[0]).into(holder.recEntryImage)
        }
        holder.recEntryTitle.text = dataList[position].entryTitle
        holder.recEntryGoal.text = dataList[position].entryGoal
        holder.recEntryClosingDate.text = dataList[position].entryClosingDate
        holder.recEntryType.text = dataList[position].entryType

        //button to view detailed entry info
        holder.viewButton.setOnClickListener {
            val intent = Intent(context, EntryDetails::class.java)
            intent.putExtra("entrydata", dataList[position])
            context.startActivity(intent)
        }

        //button to revise
        holder.updateButton.setOnClickListener {
            val intent = Intent(context, UpdateEntries::class.java)
            intent.putExtra("entrydata", dataList[position])
            context.startActivity(intent)
        }

        //button to delete entries from DB
        holder.removeButton.setOnClickListener {
            val databaseReference = FirebaseDatabase.getInstance().getReference("Entry Info")
            val key = dataList[position].entryKey
            if (key != null) {
                databaseReference.child(key).removeValue()
            }
        }

        //button to view all donations recieved for entry
        holder.donationsButton.setOnClickListener {
            val intent = Intent(context, ReadPayments::class.java)
            intent.putExtra("entrydata", dataList[position])
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}

class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var recEntryImage: ImageView
    var recEntryTitle: TextView
    var recEntryGoal: TextView
    var recEntryClosingDate: TextView
    var recEntryType: TextView
    val updateButton: Button
    val viewButton: Button
    val removeButton: Button
    val donationsButton: Button

    init {
        recEntryImage = itemView.findViewById(R.id.recEntryImage)
        recEntryType = itemView.findViewById(R.id.recEntryType)
        recEntryTitle = itemView.findViewById(R.id.recEntryTitle)
        recEntryGoal = itemView.findViewById(R.id.recEntryGoal)
        recEntryClosingDate = itemView.findViewById(R.id.recEntryClosingDate)
        updateButton = itemView.findViewById(R.id.updateCampaign)
        viewButton = itemView.findViewById(R.id.detailCampaign)
        removeButton = itemView.findViewById(R.id.deleteCampaign)
        donationsButton = itemView.findViewById(R.id.ReadPayments)
    }
}