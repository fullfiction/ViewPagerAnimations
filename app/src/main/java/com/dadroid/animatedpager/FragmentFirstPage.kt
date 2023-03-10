package com.dadroid.animatedpager

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView

class FragmentFirstPage : IntroFragment() {

    companion object {
        fun newInstance(pageNumber : Int) : FragmentFirstPage {
            return FragmentFirstPage().apply {
                arguments = Bundle().apply {
                    putInt(PAGE_NUMBER_KEY, pageNumber)
                }
            }
        }
    }

    var mIcon : AppCompatImageView? = null
    var mIcon2 : AppCompatImageView? = null
    var test : TextView? = null

    override fun color() = Color.parseColor("#634890")
    override fun layoutId() = R.layout.fragment_first_page

    @SuppressLint("CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mIcon = view.findViewById(R.id.icon)
        mIcon2 = view.findViewById(R.id.icon2)
        test = view.findViewById(R.id.test)
    }

    override fun onScroll(it: PublishSubjectMessage) {
        val pageWidthTimesPosition: Float = requireView().width * (it.position)
        val scrollFactor = pageWidthTimesPosition / 1f
        mIcon!!.translationX = scrollFactor
        mIcon!!.translationY = scrollFactor
        mIcon2!!.translationY = scrollFactor
        test?.text = it.position.toString()
    }
}