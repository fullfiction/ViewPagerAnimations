package com.dadroid.animatedpager

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable

const val PAGE_NUMBER_KEY = "page_number"

abstract class IntroFragment : Fragment() {

    private var disposable : Disposable? = null
    protected var pageNumber : Int? = null

    public abstract fun color() : Int
    protected abstract fun layoutId() : Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageNumber = arguments?.getInt(PAGE_NUMBER_KEY)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(layoutId(), null, false)
        root.tag = pageNumber
        return root
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

    abstract fun onScroll(position : PublishSubjectMessage)
}