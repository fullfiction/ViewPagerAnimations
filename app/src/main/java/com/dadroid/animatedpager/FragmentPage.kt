package com.dadroid.animatedpager

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable

private const val PAGE_NUMBER_KEY = "page_number"

class FragmentPage : Fragment() {

    companion object {
        fun newInstance(pageNumber : Int) : FragmentPage {
            return FragmentPage().apply {
                arguments = Bundle().apply {
                    putInt(PAGE_NUMBER_KEY, pageNumber)
                }
            }
        }
    }

    var pageNumber : Int? = null
    
    var mIcon : AppCompatImageView? = null
    var mIcon2 : AppCompatImageView? = null

    private var disposable : Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageNumber = arguments?.getInt(PAGE_NUMBER_KEY)
    }

    @SuppressLint("InflateParams")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_page, null, false)
        root.tag = pageNumber
        return root
    }

    @SuppressLint("CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mIcon = view.findViewById(R.id.icon)
        mIcon2 = view.findViewById(R.id.icon2)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        disposable = (activity as MainActivity).publishSubject
            .filter { it.pageNumber == pageNumber }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                try {
                    onScroll(it)
                }catch(_: IllegalStateException) {
                    // Ignore
                }
            }
    }

    override fun onDetach() {
        super.onDetach()
        if(disposable != null && !disposable!!.isDisposed) {
            disposable!!.dispose()
            disposable = null
        }
    }

    private fun onScroll(it: PublishSubjectMessage) {
        val pageWidthTimesPosition: Float = requireView().width * (it.position)
        val scrollFactor = pageWidthTimesPosition / 0.5f
        mIcon!!.translationX = scrollFactor
        mIcon!!.translationY = scrollFactor * 0.5f
        mIcon2!!.translationY = scrollFactor
    }
}