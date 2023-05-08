package com.example.caregiver.ui.model
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EntryData(
    val userId: String?=null,
    val entryKey: String? = null,
    val entryType: String? = null,
    val entryTitle: String? = null,
    val entryClosingDate: String? = null,
    val entryDescription: String? = null,
    val entryImages: MutableList<String> = mutableListOf(),
    val entryGoal: String? = null,
    val username:String?=null
) : Parcelable

