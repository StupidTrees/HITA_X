package com.stupidtree.hitax.data.source.web.service

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import retrofit2.*
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * 用于将retrofit2的请求结果Call对象适配为LiveData
 */
class LiveDataCallAdapter // 下面的 responseType 方法需要数据的类型
internal constructor(private val responseType: Type) : CallAdapter<LiveData<*>> {
    override fun responseType(): Type {
        return responseType
    }

    override fun <R> adapt(call: Call<R>): LiveData<*> {
        val result = MutableLiveData<R?>()
        call.enqueue(object : Callback<R> {
            override fun onResponse(call: Call<R>, response: Response<R>) {
                result.postValue(response.body())
            }

            override fun onFailure(call: Call<R>, t: Throwable) {
                result.postValue(null)
            }
        })
        return result
    }

    class LiveDataCallAdapterFactory : CallAdapter.Factory() {
        override fun get(returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit): CallAdapter<*>? {
            // 获取原始类型
            val rawType = getRawType(returnType)
            // 返回值必须是CustomCall并且带有泛型
            if (rawType == LiveData::class.java && returnType is ParameterizedType) {
                val callReturnType = getParameterUpperBound(0, returnType)
                return LiveDataCallAdapter(callReturnType)
            }
            return null
        }

        companion object {
            @JvmField
            val INSTANCE = LiveDataCallAdapterFactory()
        }
    }

}