package com.stupidtree.component.web

import retrofit2.Retrofit

abstract class BaseWebSource<S>(retrofit: Retrofit) {
    @JvmField
    protected var service: S
    init {
        service = retrofit.create(getServiceClass())
    }

    protected abstract fun getServiceClass(): Class<S>


}