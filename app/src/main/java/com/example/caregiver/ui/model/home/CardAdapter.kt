package com.example.caregiver.ui.model.home
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.caregiver.R
import com.example.caregiver.ui.model.Card


class CardAdapter(private val context: HomeFragment, private val mList: List<Card>): RecyclerView.Adapter<CardAdapter.ViewHolder>() {
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

        val CardModel = mList[position]

        // sets the image to the imageview from our itemHolder class
//        holder.imageView.setImageResource(CardModel.image)
        // sets the text to the textview from our itemHolder class
        holder.textView.text = CardModel.header
        holder.description.text = CardModel.description
        Glide.with(context).load(CardModel.image).into(holder.imageView)

//        GlideApp.with(this /* context */)
//            .load(storageReference)
//            .into(imageView)

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.image)
        val textView: TextView = itemView.findViewById(R.id.header)
        val description: TextView = itemView.findViewById(R.id.description)
    }
}