package com.dadroid.animatedpager

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.animation.TranslateAnimation
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import kotlin.math.abs

class FragmentSecondPage : IntroFragment() {

    companion object {
        fun newInstance(pageNumber : Int) : FragmentSecondPage {
            return FragmentSecondPage().apply {
                arguments = Bundle().apply {
                    putInt(PAGE_NUMBER_KEY, pageNumber)
                }
            }
        }
    }

    var mIcon : AppCompatImageView? = null
    var mIcon2 : AppCompatImageView? = null
    var test : TextView? = null

    var first : CardView? = null
    var second : CardView? = null
    var third : CardView? = null
    var fourth : CardView? = null


    override fun color() = R.color.second_page

    override fun layoutId()= R.layout.fragment_second_page

    @SuppressLint("CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mIcon = view.findViewById(R.id.icon)
        mIcon2 = view.findViewById(R.id.icon2)
        test = view.findViewById(R.id.test)

        first = view.findViewById<CardView>(R.id.first)
        second = view.findViewById<CardView>(R.id.second)
        third = view.findViewById<CardView>(R.id.third)
        fourth = view.findViewById<CardView>(R.id.fourth)


    }

    override fun onScroll(it: PublishSubjectMessage) {
        val pageWidthTimesPosition: Float = requireView().width * (it.position)
        var scrollFactor = pageWidthTimesPosition / 2f
        test?.text = it.position.toString()
        if(it.position == 0f)
        {
            first?.slideDown()
            second?.slideDown()
            third?.slideDown()
            fourth?.slideDown()
        }else {
            if(it.position > 0f && it.position < 1f)
                scrollFactor *= -1
            first?.translationY =scrollFactor
            second?.translationY =scrollFactor
            third?.translationY =scrollFactor
            fourth?.translationY =scrollFactor
        }
    }
}

fun View.slideDown() {
    val height = this.height
    TranslateAnimation(0f, 100f, height.toFloat(), 0f)
        .apply {
            duration = 1000
            start()
        }

}