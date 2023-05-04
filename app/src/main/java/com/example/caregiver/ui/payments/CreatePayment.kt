package com.example.caregiver.ui.payments

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.example.caregiver.R
import com.example.caregiver.databinding.ActivityCreatePaymentBinding
import com.example.caregiver.ui.model.CampPay
import com.example.caregiver.ui.model.EntryData
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CreatePayment : AppCompatActivity() {

    private lateinit var binding: ActivityCreatePaymentBinding
    private lateinit var databaseReference: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityCreatePaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val entryData = intent.getParcelableExtra<EntryData>("entrydata")
        var paymentType: String? = null

        if (entryData != null) {
            binding.tvTitle.setText(entryData.entryTitle)
            binding.tvType.setText(entryData.entryType)
        }
        if (entryData?.entryType != null) {
            binding.monthly.visibility = View.GONE
            binding.onetime.visibility = View.GONE
            paymentType = "One Time"
        }

        binding.monthly.setOnClickListener {
            paymentType = "Monthly"
            binding.monthly.setBackgroundColor(resources.getColor(R.color.white))
        }

        binding.onetime.setOnClickListener {
            paymentType = "One time"
            binding.onetime.setBackgroundColor(resources.getColor(R.color.white))
        }

        binding.createAmount.setOnEditorActionListener { _, actionID, _ ->

            if (actionID == EditorInfo.IME_ACTION_DONE) {
                val paymentAmount = binding.createAmount.text.toString()

                // Set the text of the TextView to the entered payment amount
                binding.amountView.text = "Rs.$paymentAmount"
                binding.donAmount.text = "Donation Amount"

                true
            } else {
                false
            }
        }

        val campaignID = entryData?.entryKey //hardcode
        binding.createButton.setOnClickListener {

            val payamount = binding.createAmount.text.toString()

            // Create a notification manager
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Create a notification channel (required for API level 26 and above)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    "my_channel_id", "My Channel", NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationManager.createNotificationChannel(channel)
            }


            // Build the notification
            val builder = NotificationCompat.Builder(this, "my_channel_id")
                .setSmallIcon(R.drawable.ic_launcher_foreground).setContentTitle("Payment Success")
                .setContentText("Donation amount of rupee $payamount")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            notificationManager.notify(0, builder.build())

            databaseReference = FirebaseDatabase.getInstance().getReference("Payment")

            val payID = databaseReference.push().key!!


            if (payamount.isEmpty()) {
                binding.createAmount.error = "Enter an amount"
            }


            val cpays = CampPay(payID, campaignID, payamount, paymentType)
            databaseReference.child(payID).setValue(cpays).addOnSuccessListener {

                binding.createAmount.text.clear()


                Toast.makeText(this, "Payment success", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this, "Failure", Toast.LENGTH_SHORT).show()
            }

        }

        binding.readButton.setOnClickListener {

            val intent = Intent(this, ReadPayments::class.java)
            intent.putExtra("entrydata", entryData)
            startActivity(intent)

        }
    }
}