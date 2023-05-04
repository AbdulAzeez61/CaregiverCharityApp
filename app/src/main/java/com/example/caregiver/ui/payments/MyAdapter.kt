package com.example.caregiver.ui.payments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.caregiver.R
import com.example.caregiver.ui.model.CampPay

class MyAdapter(private val context: ReadPayments, private val dataList: List<CampPay>) :
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
        holder.recPayAmount.text = dataList[position].payAmount
    }


}

class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var recType: TextView
    var recPayAmount: TextView
    var recCard: CardView

    init {
        recType = itemView.findViewById(R.id.recType)
        recPayAmount = itemView.findViewById(R.id.recPayAmount)
        recCard = itemView.findViewById(R.id.recCard)
    }

}