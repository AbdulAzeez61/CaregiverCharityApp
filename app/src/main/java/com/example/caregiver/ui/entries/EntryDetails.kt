package com.example.caregiver.ui.entries


import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.example.caregiver.R
import com.example.caregiver.databinding.ActivityEntryDetailsBinding
import com.example.caregiver.ui.model.EntryData

class EntryDetails : AppCompatActivity() {

    private lateinit var binding: ActivityEntryDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEntryDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val EntryData = intent.getParcelableExtra<EntryData>("entrydata")
        if (EntryData != null) {
            binding.entryTitle.text = EntryData.entryTitle
            binding.entryType.text = EntryData.entryType
            binding.entryDesc.text = EntryData.entryDescription
            binding.entryclosingdate.text = EntryData.entryClosingDate
            binding.entryGoal.text = EntryData.entryGoal
            val viewPager = findViewById<ViewPager>(R.id.viewPager)
            val adapter = ImagePagerAdapter(this, EntryData.entryImages)
            viewPager.adapter = adapter
        }

//        val user = true
//
//        if (user==true){
//            binding.entryType.setVisibility(View.GONE);
//        }
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