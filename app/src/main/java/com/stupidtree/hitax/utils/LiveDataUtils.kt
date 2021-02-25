package com.stupidtree.hitax.utils

import androidx.lifecycle.MutableLiveData

object LiveDataUtils {
    fun <T> getMutableLiveData(data: T? = null): MutableLiveData<T> {
        val res = MutableLiveData<T>()
        res.value = data
        return res
    }
}