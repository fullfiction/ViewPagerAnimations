package com.dadroid.animatedpager

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.widget.HorizontalScrollView
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import io.reactivex.subjects.PublishSubject
import kotlin.math.min


class MainActivity : AppCompatActivity(), ViewPager.PageTransformer, ViewPager.OnPageChangeListener {

    val publishSubject = PublishSubject.create<PublishSubjectMessage>()

    private var mViewPager : ViewPager? = null
    private var mHorizontalScrollView : HorizontalScrollView? = null
    private var bgWidth : Int = 100.dp

    override  fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bgColors = listOf("#634890", "#F7C04A", "#F16767", "#62CDFF")

        mHorizontalScrollView = findViewById(R.id.hsv)
        mViewPager = findViewById<ViewPager>(R.id.pager)
        mViewPager?.let {
            it.adapter = PagerAdapter(supportFragmentManager, bgColors.size)
            it.setPageTransformer(false, this)
            it.addOnPageChangeListener(this)
        }

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        bgWidth += displayMetrics.widthPixels

        val container = findViewById<LinearLayoutCompat>(R.id.container)

        bgColors.forEachIndexed { index, s ->

            val bgImage = AppCompatImageView(this)
            val lp = LinearLayoutCompat.LayoutParams(bgWidth!!, LayoutParams.MATCH_PARENT)
            bgImage.layoutParams = lp
            bgImage.scaleType = ImageView.ScaleType.FIT_XY
            bgImage.setImageDrawable(getDrawable(R.drawable.bg))
            bgImage.setBackgroundColor(Color.parseColor(bgColors[min(index +1, bgColors.size -1)]))
            bgImage.imageTintList = ColorStateList.valueOf(Color.parseColor(s))

            container.addView(bgImage)
        }
    }

    override fun transformPage(page: View, position: Float) {
        (page.context as MainActivity).publishSubject.onNext(PublishSubjectMessage(page.tag as Int, position))
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        val fullPage = position * bgWidth
        val scrollDistance = fullPage + (positionOffsetPixels * bgWidth / mViewPager!!.width)
        mHorizontalScrollView!!.scrollTo(scrollDistance, 0)
    }

    override fun onPageSelected(position: Int) {
    }

    override fun onPageScrollStateChanged(state: Int) {
    }
}

class PagerAdapter(fm: FragmentManager, var pageCount : Int ) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getCount(): Int {
        return pageCount
    }

    override fun getItem(position: Int): Fragment {
        return FragmentPage.newInstance(position)
    }
}

data class PublishSubjectMessage(
    val pageNumber : Int,
    val position: Float
)