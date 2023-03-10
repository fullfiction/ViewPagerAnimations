package com.dadroid.animatedpager

import android.content.res.ColorStateList
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


class MainActivity() : AppCompatActivity(), ViewPager.PageTransformer, ViewPager.OnPageChangeListener {

    val publishSubject = PublishSubject.create<PublishSubjectMessage>()

    private var mViewPager : ViewPager? = null
    private var mViewPagerAdapter: PagerAdapter? = null
    private var mHorizontalScrollView : HorizontalScrollView? = null

    private var bgWidth : Int = 100.dp

    override  fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawPager()
        drawBackground()
    }

    private fun drawPager() {
        mViewPager = findViewById(R.id.pager)
        mViewPagerAdapter = PagerAdapter(supportFragmentManager)
        mViewPagerAdapter?.let { it ->
            it.addPage(FragmentFirstPage.newInstance(0))
            it.addPage(FragmentSecondPage.newInstance(1))
            it.addPage(FragmentThirdPage.newInstance(2))
            it.addPage(FragmentFourthPage.newInstance(3))

            mViewPager?.let {
                it.adapter = mViewPagerAdapter
                it.setPageTransformer(false, this)
                it.addOnPageChangeListener(this)
            }
        }
    }

    private fun drawBackground() {
        mHorizontalScrollView = findViewById(R.id.hsv)
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        bgWidth += displayMetrics.widthPixels
        val container = findViewById<LinearLayoutCompat>(R.id.container)
        val bgColors = mViewPagerAdapter!!.getColors()
        bgColors.forEachIndexed { index, color ->

            val bgImage = AppCompatImageView(this)
            val lp = LinearLayoutCompat.LayoutParams(bgWidth, LayoutParams.MATCH_PARENT)
            bgImage.layoutParams = lp
            bgImage.scaleType = ImageView.ScaleType.FIT_XY
            bgImage.setImageDrawable(getDrawable(R.drawable.bg))
            bgImage.setBackgroundColor(bgColors[min(index + 1, bgColors.size - 1)])
            bgImage.imageTintList = ColorStateList.valueOf(color)

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

class PagerAdapter constructor(fm: FragmentManager, private var pages : MutableList<IntroFragment> = mutableListOf())
    : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    fun addPage(page : IntroFragment){
        pages.add(page)
        notifyDataSetChanged()
    }

    fun getColors() = pages.map { it.color() }

    override fun getCount(): Int {
        return pages.size
    }

    override fun getItem(position: Int): Fragment {
        return pages[position]
    }
}

data class PublishSubjectMessage(
    val pageNumber : Int,
    val position: Float
)