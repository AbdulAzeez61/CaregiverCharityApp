package com.example.caregiver.ui.home

import android.content.Context
import android.content.Intent
import android.media.Image
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.caregiver.R
import com.example.caregiver.ui.entries.EntryDetails
import com.example.caregiver.ui.model.EntryData


class CardAdapter(private val context: Context, private var mList: List<EntryData>) :
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
        /**
         * ge the data set and assign to layout
         */
        if (mList[position].entryImages.isNotEmpty()) {
            Glide.with(context).load(mList[position].entryImages[0]).into(holder.imageView)
        }

        holder.textView.text = mList[position].entryTitle
        val maxDescriptionLength = 300
        val descriptionText = mList[position].entryDescription
        if (descriptionText?.length!! > maxDescriptionLength) {
            holder.description.text = descriptionText?.substring(0, maxDescriptionLength) + "..."
        } else {
            holder.description.text = descriptionText
        }

//        holder.createdTime.text = mList[position].relativeTime
        holder.username.text = mList[position].username
//            Glide.with(context).load(mList[position].entryImages[0]).into(holder.imageView)
        if (mList[position].profileImg != null) {
            Glide.with(context)
                .load(mList[position].profileImg)
                .into(holder.profileImg)
        }


        holder.donate.setOnClickListener {
            val intent = Intent(context, EntryDetails::class.java)
            intent.putExtra("entrydata", mList[position])
            context.startActivity(intent)
        }

//        GlideApp.with(this /* context */)
//            .load(storageReference)
//            .into(imageView)
    }

    /**
     * search functions
     */
    fun searchProjects(searchList: List<EntryData>) {
        mList = searchList
        notifyDataSetChanged()
    }


    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.image)
        val profileImg: ImageView = itemView.findViewById(R.id.profileImg)
        val createdTime: TextView = itemView.findViewById(R.id.createdTime)
        val username: TextView = itemView.findViewById(R.id.username)
        val textView: TextView = itemView.findViewById(R.id.header)
        val description: TextView = itemView.findViewById(R.id.description)
        val donate: TextView = itemView.findViewById(R.id.DonateButton)
        val share: ImageView = itemView.findViewById(R.id.share)

        init {
            share.setOnClickListener {
                val postText = "Join with caregiver and help the world"
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_TEXT, postText)
                itemView.context.startActivity(Intent.createChooser(shareIntent, "Share post via"))
            }
        }
    }
}