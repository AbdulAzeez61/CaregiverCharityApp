package com.example.caregiver.ui.entries

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.caregiver.R
import com.example.caregiver.databinding.ActivityCreateEntryBinding
import com.example.caregiver.ui.model.EntryData
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*

class CreateEntry : AppCompatActivity() {

    private lateinit var binding: ActivityCreateEntryBinding
    private val REQUEST_CODE_PICK_IMAGES = 7
    private lateinit var firebaseAuth: FirebaseAuth
    private var userId: String? = null
    private var profileImg: String? = null
    private lateinit var databaseReference: DatabaseReference
    var username: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        var user = firebaseAuth.currentUser
        userId = user?.uid
        username = user?.displayName
        profileImg = user?.photoUrl.toString()

        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId!!)

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userRole = dataSnapshot.child("role").getValue(String::class.java)
                Log.d("CreateEntry", "Message Check Userole Is Who inside " + userRole.toString())

                if (userRole != null && userRole == "Individual") {
                    binding.SetUp.text = "Set up Fundraiser"
                    binding.campaignType.visibility = View.GONE
                    binding.createButton.text = "Set up Fundraiser"

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

        val myCalendar = Calendar.getInstance()
        val editText = binding.campaignclosingdate
        val date = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLabel(myCalendar, editText)
        }

        editText.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                this@CreateEntry,
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

        binding.createButton.setOnClickListener {
            val storageReference = FirebaseStorage.getInstance().getReference("campaign images")
            val uploadedImageUrls = mutableListOf<Uri>()

            if (selectedImages.isNotEmpty()) {
                val uploadTasks = mutableListOf<Task<Uri>>()
                for (uri in selectedImages) {
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
                    uploadedImageUrls.addAll(urls)
                    // Now you can access the uploadedImageUrls list and assign it to campaignImages
                    createCampaignEntry(uploadedImageUrls)
                }
            } else {
                // No images selected, create campaign entry with empty campaignImages list
                createCampaignEntry(uploadedImageUrls)
            }
        }
    }

    fun createCampaignEntry(campaignImages: List<Uri>) {
        val entryType = binding.campaignType.text.toString()
        val entryTitle = binding.campaignTitle.text.toString()
        val entryClosingDate = binding.campaignclosingdate.text.toString()
        val entryDescription = binding.campaignDesc.text.toString()
        val entryGoal = binding.campaignGoal.text.toString()

        val uploadedimageUrls = mutableListOf<String>()
        for (uri in campaignImages) {
            uploadedimageUrls.add(uri.toString())
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("Entry Info")
        val newEntryRef = databaseReference.push()
        val entryKey = newEntryRef.key
        val entry = EntryData(
            userId,
            entryKey,
            entryType,
            entryTitle,
            entryClosingDate,
            entryDescription,
            entryImages = uploadedimageUrls,
            entryGoal,
            username,
            profileImg,
        )
        newEntryRef.setValue(entry).addOnSuccessListener {
            newEntryRef.child("createdTime").setValue(ServerValue.TIMESTAMP)
        }

        newEntryRef.setValue(entry).addOnSuccessListener {
//            binding.campaignTitle.text.clear()
//            binding.campaignType.text?.clear()
//            binding.campaignclosingdate.text.clear()
//            binding.campaignDesc.text.clear()
//            binding.campaignGoal.text.clear()
//            binding.viewPager.removeAllViews()
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()

            val intent = Intent(this@CreateEntry, AllEntries::class.java)
            startActivity(intent)
            finish()
        }.addOnFailureListener {
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateLabel(myCalendar: Calendar, editText: EditText) {
        val myFormat = "dd/MM/yy"
        val sdf = SimpleDateFormat(myFormat, Locale.ENGLISH)
        editText.setText(sdf.format(myCalendar.time))
    }

    private val selectedImages = mutableListOf<Uri>()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_PICK_IMAGES && resultCode == Activity.RESULT_OK) {
            val clipData = data?.clipData
            if (clipData != null) {
                // multiple images selected
                for (i in 0 until clipData.itemCount) {
                    val uri = clipData.getItemAt(i).uri
                    selectedImages.add(uri)
                }
            } else {
                // single image selected
                val uri = data?.data
                if (uri != null) {
                    selectedImages.add(uri)
                }
            }
        }
        val viewPager = findViewById<ViewPager>(R.id.viewPager)
        val adapter = ImageAdapter(this, selectedImages, selectedImages)
        viewPager.adapter = adapter
    }

    class ImageAdapter(
        private val context: Context,
        private val images: List<Uri>,
        private val selectedImages: MutableList<Uri>
    ) : PagerAdapter() {

        override fun getCount(): Int = images.size

        override fun isViewFromObject(view: View, `object`: Any): Boolean = view == `object`

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val view = LayoutInflater.from(context).inflate(R.layout.image_item, container, false)
            val imageView = view.findViewById<ImageView>(R.id.imageView)
            val closeButton = view.findViewById<ImageButton>(R.id.closeButton)

            imageView.setImageURI(images[position])
            closeButton.setOnClickListener {
                selectedImages.removeAt(position)
                notifyDataSetChanged()
            }

            container.addView(view)
            return view
        }

        override fun getItemPosition(`object`: Any): Int {
            return POSITION_NONE
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }
    }
}

