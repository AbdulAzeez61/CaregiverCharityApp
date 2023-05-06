package com.example.caregiver

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import androidx.test.core.app.ApplicationProvider
import androidx.viewpager.widget.ViewPager
import com.example.caregiver.databinding.ActivityCreateEntryBinding
import com.example.caregiver.databinding.ActivityUpdateEntriesBinding
import com.example.caregiver.ui.entries.CreateEntry
import com.example.caregiver.ui.entries.UpdateEntries
import com.example.caregiver.ui.model.EntryData
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ExampleUnitTest {
    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        FirebaseApp.initializeApp(context)
        val auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword("abdazeez@gmail.com", "abcd123")
    }
    @Test
    fun testCreateCampaignEntry() {

        val controller = Robolectric.buildActivity(CreateEntry::class.java)
        val myClass = controller.get()
        controller.create().start().resume()

        val bindingField = CreateEntry::class.java.getDeclaredField("binding")
        bindingField.isAccessible = true
        val binding = ActivityCreateEntryBinding.inflate(LayoutInflater.from(myClass))
        bindingField.set(myClass, binding)

//        myClass.setContentView(binding.root)
        val campaignImages = listOf<Uri>()
        val entryType = "Test Type"
        val entryTitle = "Test Title"
        val entryClosingDate = "Test Date"
        val entryDescription = "Test Description"
        val entryGoal = "Test Goal"

        binding.campaignType.setText(entryType)
        binding.campaignTitle.setText(entryTitle)
        binding.campaignclosingdate.setText(entryClosingDate)
        binding.campaignDesc.setText(entryDescription)
        binding.campaignGoal.setText(entryGoal)

        myClass.createCampaignEntry(campaignImages)

        val databaseReference = FirebaseDatabase.getInstance().getReference("Entry Info")

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val entries =
                    dataSnapshot.children.mapNotNull { it.getValue(EntryData::class.java) }
                assertNotNull(entries)
                assertEquals(1, entries.size)
                val entry = entries[0]
                assertEquals(entryType, entry.entryType)
                assertEquals(entryTitle, entry.entryTitle)
                assertEquals(entryClosingDate, entry.entryClosingDate)
                assertEquals(entryDescription, entry.entryDescription)
                assertEquals(entryGoal, entry.entryGoal)
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }
    @Test
    fun testUpdateCampaignEntry() {
        val entryData = EntryData(
            userId = "dummyUserId",
            entryKey = "dummyKey",
            entryType = "dummyType",
            entryTitle = "dummyTitle",
            entryClosingDate = "dummyClosingDate",
            entryDescription = "dummyDescription",
            entryImages = mutableListOf("dummyImage1", "dummyImage2"),
            entryGoal = "dummyGoal"
        )
        val intent = Intent().apply {
            putExtra("entrydata", entryData)
        }

        val controller = Robolectric.buildActivity(UpdateEntries::class.java,intent)
        val myClass = controller.get()
        controller.create().start().resume()

        val bindingField = UpdateEntries::class.java.getDeclaredField("binding")
        bindingField.isAccessible = true
        val binding = ActivityUpdateEntriesBinding.inflate(LayoutInflater.from(myClass))
        bindingField.set(myClass, binding)

        val viewPager = ViewPager(myClass)
        viewPager.id = R.id.viewPager
        myClass.setContentView(viewPager)

// Initialize the adapter using reflection
        val adapterField = UpdateEntries::class.java.getDeclaredField("adapter")
        adapterField.isAccessible = true
        val adapter = UpdateEntries.ImagePagerAdapter(myClass, entryData.entryImages)
        adapterField.set(myClass, adapter)

        viewPager.adapter = adapter
        val remoteImages = mutableListOf<String>()
        val entryType = "Updated Type"
        val entryTitle = "Updated Title"
        val entryClosingDate = "Updated Date"
        val entryDescription = "Updated Description"
        val entryGoal = "Updated Goal"

        binding.entryType.setText(entryType)
        binding.entryTitle.setText(entryTitle)
        binding.entryclosingdate.setText(entryClosingDate)
        binding.entryDesc.setText(entryDescription)
        binding.entryGoal.setText(entryGoal)

        myClass.updateCampaignEntry(remoteImages)

        val databaseReference = FirebaseDatabase.getInstance().getReference("Entry Info")

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val entries =
                    dataSnapshot.children.mapNotNull { it.getValue(EntryData::class.java) }
                assertNotNull(entries)
                assertEquals(1, entries.size)
                val entry = entries[0]
                assertEquals(entryType, entry.entryType)
                assertEquals(entryTitle, entry.entryTitle)
                assertEquals(entryClosingDate, entry.entryClosingDate)
                assertEquals("entryDescription", entry.entryDescription)
            }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }
}