package com.example.caregiver.ui.entries


import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.example.caregiver.R
import com.example.caregiver.databinding.ActivityEntryDetailsBinding
import com.example.caregiver.ui.model.EntryData
import com.example.caregiver.ui.payments.CreatePayment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlin.math.roundToInt

class EntryDetails : AppCompatActivity() {

    private lateinit var binding: ActivityEntryDetailsBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEntryDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        var user = firebaseAuth.currentUser

        val EntryData = intent.getParcelableExtra<EntryData>("entrydata")

        if (EntryData != null) {
            binding.entryTitle.text = EntryData.entryTitle
            binding.entryType.text = EntryData.entryType
            binding.entryDesc.text = EntryData.entryDescription
            binding.entryclosingdate.text = EntryData.entryClosingDate
            binding.entryGoal.text = EntryData.entryGoal
            binding.username.text = EntryData.username

            if (EntryData.profileImg != null) {
                Glide.with(this).load(EntryData.profileImg).into(binding.profileImg)
            }

            val viewPager = findViewById<ViewPager>(R.id.viewPager)
            val adapter = ImagePagerAdapter(this, EntryData.entryImages)
            viewPager.adapter = adapter
        }

        if (EntryData!!.entryType == "") {
            binding.entryType.visibility = View.GONE
        }

        if (EntryData.userId == user?.uid) {
            binding.DonateButton.isEnabled = false
        }

        val campaignID = EntryData.entryKey //hardcode
        var totalPaymentAmount = 0.0

        databaseReference = FirebaseDatabase.getInstance().getReference("Payment")

        databaseReference.orderByChild("cid")?.equalTo(campaignID)
            ?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Iterate over all payments with the given campaignID
                    for (itemSnapshot in snapshot.children) {
                        val payAmountString = itemSnapshot.child("payAmount").value.toString()
                        val payAmount = payAmountString.toDouble()

                        // Add the payAmount to the total payment amount
                        totalPaymentAmount += payAmount
                    }


                    binding.tvProgress.text = " raised Rs.${totalPaymentAmount.roundToInt()}"

                    // Display the total payment amount for the given campaignID
                    Log.d(
                        ContentValues.TAG,
                        "Total payment amount for campaign $campaignID: $totalPaymentAmount"
                    )
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(ContentValues.TAG, "Failed to read value.", error.toException())
                }
            })
        binding.username.movementMethod = LinkMovementMethod.getInstance()
        binding.DonateButton.setOnClickListener {
            val intent = Intent(this, CreatePayment::class.java)
            intent.putExtra("entrydata", EntryData)
            this.startActivity(intent)
        }
        binding.username.setOnClickListener {
            val intent = Intent(this, AllEntriesByUserName::class.java)
            intent.putExtra("profileImg", EntryData.profileImg)
            this.startActivity(intent)
        }

    }

    class ImagePagerAdapter(
        private val context: Context, private val imageUris: MutableList<String>
    ) : PagerAdapter() {

        override fun getCount(): Int = imageUris.size

        override fun isViewFromObject(view: View, `object`: Any): Boolean = view == `object`

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val imageView = ImageView(context)
            imageView.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            Glide.with(context).load(imageUris[position]).into(imageView)
            container.addView(imageView)
            return imageView
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }
    }
}
