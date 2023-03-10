package com.dadroid.animatedpager

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.widget.HorizontalScrollView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import io.reactivex.subjects.PublishSubject
import kotlin.math.min


class MainActivity : AppCompatActivity(), ViewPager.PageTransformer, ViewPager.OnPageChangeListener {

    val publishSubject = PublishSubject.create<PublishSubjectMessage>()

    var mViewPager : ViewPager? = null
    var mHorizontalScrollView : HorizontalScrollView? = null

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
        val width = displayMetrics.widthPixels

        val container = findViewById<LinearLayoutCompat>(R.id.container)

        bgColors.forEachIndexed { index, s ->
            val page = View(this)
            page.layoutParams = LinearLayoutCompat.LayoutParams(width, height)
            page.setBackgroundColor(Color.parseColor(s))
            container.addView(page)

            val arc = View(this)
            val arcBg = getDrawable(R.drawable.arc_bg) as LayerDrawable
            val arcBgFrontLayer =
                arcBg.findDrawableByLayerId(R.id.frontLayer) as GradientDrawable
            arcBgFrontLayer.setColor(Color.parseColor(s))
            val arcBgBackLayer =
                arcBg.findDrawableByLayerId(R.id.backLayer) as GradientDrawable
            arcBgBackLayer.setColor(Color.parseColor(bgColors[min(index +1, bgColors.size -1)]))
            arc.background = arcBg

            val layoutParams = LinearLayoutCompat.LayoutParams(100.dp, LayoutParams.MATCH_PARENT)
            arc.layoutParams = layoutParams
            container.addView(arc)
        }
    }

    override fun transformPage(page: View, position: Float) {
        (page.context as MainActivity).publishSubject.onNext(PublishSubjectMessage(page.tag as Int, position))
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        val x = positionOffsetPixels + (position * (mViewPager!!.width + 100.dp))
        mHorizontalScrollView!!.scrollTo(x, 0)
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