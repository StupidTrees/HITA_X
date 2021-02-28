package com.stupidtree.hitax.utils

import androidx.annotation.MainThread
import androidx.arch.core.util.Function
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import java.util.function.BiFunction

object MTransformations {
    @MainThread
    fun <X, Y> map(
            source: LiveData<X>,
            mapFunction: Function<X, Y>): MediatorLiveData<Y> {
        val result = MediatorLiveData<Y>()
        result.addSource(source) { x -> result.value = mapFunction.apply(x) }
        return result
    }

    @MainThread
    fun <X, Y> switchMap(
            source: LiveData<X>,
            switchMapFunction: Function<X, LiveData<Y>>): MediatorLiveData<Y> {
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
                    result.addSource(mSource!!) { y -> result.setValue(y) }
                }
            }
        })
        return result
    }

    @MainThread
    fun <X, X2, Y> switchMap(
            source: LiveData<X>,
            source2: LiveData<X2>,
            switchMapFunction: Function<Pair<X, X2>, LiveData<Y>>): MediatorLiveData<Y> {
        val temp = MediatorLiveData<Pair<X, X2>>()
        temp.addSource(source) {
            source2.value?.let { s2 ->
                temp.value = it to s2
            }

        }
        temp.addSource(source2) {
            source.value?.let { s1 ->
                temp.value = s1 to it
            }

        }
        return switchMap(temp, switchMapFunction)
    }

    @MainThread
    fun <X, X2, Y> map(
            source: LiveData<X>,
            source2: LiveData<X2>,
            mapFunction: Function<Pair<X, X2>, Y>): MediatorLiveData<Y> {
        val temp = MediatorLiveData<Pair<X, X2>>()
        temp.addSource(source) {
            source2.value?.let { s2 ->
                temp.value = it to s2
            }

        }
        temp.addSource(source2) {
            source.value?.let { s1 ->
                temp.value = s1 to it
            }

        }
        return map(temp, mapFunction)
    }

}