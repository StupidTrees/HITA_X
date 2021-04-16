package com.stupidtree.component.web

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import retrofit2.*
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * 用于将retrofit2的请求结果Call对象适配为LiveData
 */
class LiveDataCallAdapter // 下面的 responseType 方法需要数据的类型
internal constructor(private val responseType: Type) : CallAdapter<Any,LiveData<*>> {
    override fun responseType(): Type {
        return responseType
    }

    class LiveDataCallAdapterFactory : CallAdapter.Factory() {
        override fun get(returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit): CallAdapter<Any,*>? {
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

    override fun adapt(call: Call<Any>): LiveData<*> {
        val result = MutableLiveData<Any?>()
        call.enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                result.postValue(response.body())
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                Log.e("request failed", t.toString())
                result.postValue(null)
            }
        })
        return result
    }

}