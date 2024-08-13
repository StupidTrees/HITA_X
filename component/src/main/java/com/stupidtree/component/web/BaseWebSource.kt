package com.stupidtree.component.web

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.net.ssl.SSLSocketFactory

abstract class BaseWebSource<S>(
    context: Context,
    baseUrl: String = "https://hita.store:39999",
    livedata: Boolean = true,
    https: Boolean = true
) {
//    private var sslSocketFactory: SSLSocketFactory =
//        SslContextFactory().getSslSocket(context)!!.socketFactory
    private var okHttpClient: OkHttpClient.Builder =
        OkHttpClient.Builder()//.sslSocketFactory(sslSocketFactory)

    @JvmField
    protected var service: S

    init {
        var builder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(baseUrl)
        if (https) {
            builder = builder.client(okHttpClient.build())
        }
        if (livedata) {
            builder =
                builder.addCallAdapterFactory(LiveDataCallAdapter.LiveDataCallAdapterFactory.INSTANCE)
        }
        service = builder.build().create(getServiceClass())
    }

    protected abstract fun getServiceClass(): Class<S>


}