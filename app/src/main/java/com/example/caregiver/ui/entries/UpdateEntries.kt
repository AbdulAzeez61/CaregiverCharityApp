package com.example.caregiver.ui.entries


import android.app.Activity
import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.example.caregiver.R
import com.example.caregiver.databinding.ActivityUpdateEntriesBinding
import com.example.caregiver.ui.model.EntryData
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class UpdateEntries : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateEntriesBinding
    private lateinit var databaseReference: DatabaseReference
    private val REQUEST_CODE_PICK_IMAGES = 7
    private lateinit var adapter: ImagePagerAdapter
    private lateinit var firebaseAuth: FirebaseAuth
    private val localImages = mutableListOf<Uri>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateEntriesBinding.inflate(layoutInflater)
        setContentView(binding.root)


        firebaseAuth = FirebaseAuth.getInstance()
        var user = firebaseAuth.currentUser
        val userId = user?.uid

        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId!!)

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userRole = dataSnapshot.child("role").getValue(String::class.java)
                // Do something with userRole
                if (userRole != null && userRole == "Individual") {
                    binding.updatePageTitle.text = "Revise Fundraiser"

                    binding.entryType.visibility = View.GONE
                    binding.reviseButton.text = "Revise fundraiser"
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
            }
        })

        binding.selectImagesButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            }
            startActivityForResult(intent, REQUEST_CODE_PICK_IMAGES)
        }

        val entryData = intent.getParcelableExtra<EntryData>("entrydata")

        if (entryData != null) {
            binding.entryTitle.setText(entryData.entryTitle)
            binding.entryDesc.setText(entryData.entryDescription)
            binding.entryGoal.setText(entryData.entryGoal)
            binding.entryType.setText(entryData.entryType)
            binding.entryclosingdate.setText(entryData.entryClosingDate)
        }


        val myCalendar = Calendar.getInstance()
        val editText = binding.entryclosingdate
        val date = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLabel(myCalendar, editText)
        }

        editText.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                this@UpdateEntries,
                date,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            )
            val minDate = Calendar.getInstance()
            minDate.add(Calendar.DAY_OF_MONTH, 3)
            datePickerDialog.datePicker.minDate = minDate.timeInMillis
            datePickerDialog.show()
        }
        val viewPager = findViewById<ViewPager>(R.id.viewPager)
        adapter = ImagePagerAdapter(this, entryData!!.entryImages)
        viewPager.adapter = adapter

        binding.reviseButton.setOnClickListener {
            val remoteImages =
                entryData.entryImages.filter { it.startsWith("https://") }.toMutableList()
            val storageReference = FirebaseStorage.getInstance().getReference("campaign images")
            if (localImages.isNotEmpty()) {
                val uploadTasks = mutableListOf<Task<Uri>>()
                for (uri in localImages) {
                    val fileReference = storageReference.child(uri.lastPathSegment ?: "image.jpg")
                    val uploadTask = fileReference.putFile(uri).continueWithTask { task ->
                        if (!task.isSuccessful) {
                            task.exception?.let { throw it }
                        }
                        fileReference.downloadUrl
                    }
                    uploadTasks.add(uploadTask)
                }

                Tasks.whenAllSuccess<Uri>(uploadTasks).addOnSuccessListener { urls ->
                    for (url in urls) {
                        remoteImages.add(url.toString())
                    }
                    // Now you can access the uploadedImageUrls list and assign it to campaignImages
                    updateCampaignEntry(remoteImages)
                }
            } else {
                // No images selected, create campaign entry with empty campaignImages list
                updateCampaignEntry(remoteImages)
            }
        }
    }

    fun updateCampaignEntry(remoteImages: MutableList<String>) {
        val entryData = intent.getParcelableExtra<EntryData>("entrydata")
        databaseReference = FirebaseDatabase.getInstance().getReference("Entry Info")
        val entryRef = databaseReference.child(entryData!!.entryKey!!)
        val entryType = binding.entryType.text.toString()
        val entryTitle = binding.entryTitle.text.toString()
        val entryClosingDate = binding.entryclosingdate.text.toString()
        val entryDescription = binding.entryDesc.text.toString()
        val entryGoal = binding.entryGoal.text.toString()

        entryRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val entryData = snapshot.getValue(EntryData::class.java)
                if (entryData != null) {
                    // Update the properties of the campaign entry that have changed
                    val updates = mutableMapOf<String, Any>()
                    if (entryData.entryTitle != entryTitle) {
                        updates["entryTitle"] = entryTitle
                    }
                    if (entryData.entryType != entryType) {
                        updates["entryType"] = entryType
                    }
                    if (entryData.entryClosingDate != entryClosingDate) {
                        updates["entryClosingDate"] = entryClosingDate
                    }
                    if (entryData.entryDescription != entryDescription) {
                        updates["entryDescription"] = entryDescription
                    }
                    if (entryData.entryGoal != entryGoal) {
                        updates["entryGoal"] = entryGoal
                    }
//                    if (entryData.entryType != entryType) {
//                        updates["entryType"] = entryType
//                    }
                    if (remoteImages != entryData.entryImages) {
                        updates["entryImages"] = remoteImages
                    }
                    // Update the database with the new values
                    if (updates.isNotEmpty()) {
                        entryRef.updateChildren(updates).addOnSuccessListener {
//                            binding.entryTitle.text?.clear()
//                            binding.entryType.text?.clear()
//                            binding.entryclosingdate.text?.clear()
//                            binding.entryDesc.text?.clear()
//                            binding.entryGoal.text?.clear()
//                            binding.viewPager.removeAllViews()

                            // Create a notification manager
                            val notificationManager =
                                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                            // Create a notification channel (required for API level 26 and above)
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                val channel = NotificationChannel(
                                    "my_channel_id",
                                    "My Channel",
                                    NotificationManager.IMPORTANCE_DEFAULT
                                )
                                notificationManager.createNotificationChannel(channel)
                            }


                            // Build the notification
                            val builder =
                                NotificationCompat.Builder(this@UpdateEntries, "my_channel_id")
                                    .setSmallIcon(R.drawable.ic_launcher_foreground)
//                                    .setContentTitle("Project Updated")

                            if (entryData.entryType == "") {
                                builder.setContentText("Fundraiser updated $entryTitle")
                            } else {
                                builder.setContentText("Campaign updated $entryTitle")
                            }
//                                .setContentText("Project updated $entryTitle")
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                            notificationManager.notify(0, builder.build())





                            Toast.makeText(this@UpdateEntries, "Updated", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@UpdateEntries, AllEntries::class.java)
                            startActivity(intent)
                            finish()
                        }.addOnFailureListener { e ->
                            Toast.makeText(this@UpdateEntries, "Failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }


    private fun updateLabel(myCalendar: Calendar, editText: EditText) {
        val myFormat = "dd/MM/yy"
        val sdf = SimpleDateFormat(myFormat, Locale.ENGLISH)
        editText.setText(sdf.format(myCalendar.time))
    }

    class ImagePagerAdapter(
        private val context: Context,
        private val images: MutableList<String>,

        ) : PagerAdapter() {
        override fun getCount(): Int = images.size

        override fun isViewFromObject(view: View, obj: Any): Boolean = view == obj

        fun addImage(uri: Uri) {
            images.add(uri.toString())
            notifyDataSetChanged()
        }

        fun removeImage(position: Int) {
            images.removeAt(position)
            notifyDataSetChanged()
        }

//        fun getImages(): MutableList<String> {
//            return images
//        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val view = LayoutInflater.from(context).inflate(R.layout.image_item, container, false)
            val imageView = view.findViewById<ImageView>(R.id.imageView)
            val closeButton = view.findViewById<ImageButton>(R.id.closeButton)
            Glide.with(context).load(images[position]).into(imageView)
            closeButton.setOnClickListener {
                if (position < images.size) {
                    removeImage(position)
                    notifyDataSetChanged() // Notify the adapter that the data set has changed
                }
            }
            container.addView(view)
            return view
        }

        override fun getItemPosition(`object`: Any): Int {
            return POSITION_NONE
        }

        override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
            container.removeView(obj as View)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_PICK_IMAGES && resultCode == Activity.RESULT_OK) {
            val clipData = data?.clipData
            if (clipData != null) {
                // multiple images selected
                for (i in 0 until clipData.itemCount) {
                    val uri = clipData.getItemAt(i).uri
                    localImages.add(uri)
                    adapter.addImage(uri)
                }
            } else {
                // single image selected
                val uri = data?.data
                if (uri != null) {
                    localImages.add(uri)
                    adapter.addImage(uri)
                }
            }
        }
    }
}
