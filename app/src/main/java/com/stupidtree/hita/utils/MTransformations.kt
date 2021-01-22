package com.stupidtree.hita.utils

import androidx.annotation.MainThread
import androidx.arch.core.util.Function
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer

object MTransformations {
    @MainThread
    fun <X, Y> map(
            source: LiveData<X>,
            mapFunction: Function<X?, Y>): MediatorLiveData<Y> {
        val result = MediatorLiveData<Y>()
        result.addSource(source) { x -> result.value = mapFunction.apply(x) }
        return result
    }

    @MainThread
    fun <X, Y> switchMap(
            source: LiveData<X>,
            switchMapFunction: Function<X?, LiveData<Y>>): MediatorLiveData<Y> {
        val result = MediatorLiveData<Y>()
        result.addSource(source, object : Observer<X?> {
            var mSource: LiveData<Y>? = null
            override fun onChanged(x: X?) {
                val newLiveData = switchMapFunction.apply(x)
                if (mSource === newLiveData) {
                    return
                }
                if (mSource != null) {
                    result.removeSource(mSource!!)
                }
                mSource = newLiveData
                if (mSource != null) {
                    result.addSource(mSource!!, { y -> result.setValue(y) })
                }
            }
        })
        return result
    }
}