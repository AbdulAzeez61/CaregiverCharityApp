package com.example.caregiver.ui.home

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.caregiver.R
import com.example.caregiver.ui.entries.EntryDetails
import com.example.caregiver.ui.model.EntryData


class CardAdapter(private val context: Context, private val mList: List<EntryData>) :
    RecyclerView.Adapter<CardAdapter.ViewHolder>() {
    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.charity_card, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

//        val CardModel = mList[position]

        // sets the image to the imageview from our itemHolder class
//        holder.imageView.setImageResource(CardModel.image)
        // sets the text to the textview from our itemHolder class
        if (mList[position].entryImages.isNotEmpty()) {
            Glide.with(context).load(mList[position].entryImages[0]).into(holder.imageView)
        }
        holder.textView.text = mList[position].entryTitle
        holder.description.text = mList[position].entryDescription
//            Glide.with(context).load(mList[position].entryImages[0]).into(holder.imageView)

        holder.donate.setOnClickListener {
            val intent = Intent(context, EntryDetails::class.java)
            intent.putExtra("entrydata", mList[position])
            context.startActivity(intent)
        }

//        GlideApp.with(this /* context */)
//            .load(storageReference)
//            .into(imageView)
    }


    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.image)
        val textView: TextView = itemView.findViewById(R.id.header)
        val description: TextView = itemView.findViewById(R.id.description)
        val donate: Button = itemView.findViewById(R.id.DonateButton)
    }
}