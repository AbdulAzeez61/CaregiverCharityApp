package com.example.caregiver.ui.payments

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.caregiver.R
import com.example.caregiver.ui.model.CampPay

class MyAdapter(private val context: Context, private val dataList: List<CampPay>) :
    RecyclerView.Adapter<MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.payment_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {

        return dataList.size

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.recType.text = dataList[position].payType
        if(dataList[position].payType == null){
            holder.recType.visibility = View.GONE
        }
        holder.recPayAmount.text = dataList[position].payAmount
        holder.recName.text = dataList[position].username
    }
}

class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var recType: TextView
    var recPayAmount: TextView
    var recCard: CardView
    var recName : TextView

    init {
        recType = itemView.findViewById(R.id.recType)
        recPayAmount = itemView.findViewById(R.id.recPayAmount)
        recCard = itemView.findViewById(R.id.recCard)
        recName = itemView.findViewById(R.id.Username)
    }

}