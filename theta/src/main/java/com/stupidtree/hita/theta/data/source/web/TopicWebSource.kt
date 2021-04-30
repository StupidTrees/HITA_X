package com.stupidtree.hita.theta.data.source.web

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.stupidtree.component.data.DataState
import com.stupidtree.component.web.BaseWebSource
import com.stupidtree.component.web.ApiResponse
import com.stupidtree.hita.theta.data.model.Topic
import com.stupidtree.hita.theta.data.source.web.service.TopicService
import com.stupidtree.stupiduser.data.model.UserProfile
import com.stupidtree.stupiduser.data.source.web.service.ProfileService
import com.stupidtree.stupiduser.data.source.web.service.codes.SUCCESS
import com.stupidtree.stupiduser.data.source.web.service.codes.TOKEN_INVALID
import com.stupidtree.stupiduser.data.model.FollowResult
import com.stupidtree.stupiduser.data.source.web.service.codes
import com.stupidtree.stupiduser.util.HttpUtils
import okhttp3.MultipartBody
import retrofit2.Call
import java.util.*

/**
 * 层次：DataSource
 * 用户的数据源
 * 类型：网络数据
 * 数据：异步读，异步写
 */
class TopicWebSource(context: Context) : BaseWebSource<TopicService>(
    context
) {
    override fun getServiceClass(): Class<TopicService> {
        return TopicService::class.java
    }

    fun getTopics(
        token: String,
        mode: String,
        pageSize: Int,
        pageNum: Int,
        extra: String
    ): LiveData<DataState<List<Topic>>> {
        return Transformations.map(
            service.getTopics(
                HttpUtils.getHeaderAuth(token), mode, pageSize, pageNum, extra
            )
        ) { input ->
            if (input != null) {
                when (input.code) {
                    SUCCESS -> return@map DataState(input.data ?: listOf())
                    codes.TOKEN_INVALID -> return@map DataState(DataState.STATE.TOKEN_INVALID)
                    else -> return@map DataState(
                        DataState.STATE.FETCH_FAILED,
                        input.message
                    )
                }
            }
            DataState(DataState.STATE.FETCH_FAILED)
        }
    }

    fun getTopic(
        token: String,
        topicId: String
    ): LiveData<DataState<Topic>> {
        return Transformations.map(
            service.getTopic(
                HttpUtils.getHeaderAuth(token), topicId
            )
        ) { input ->
            if (input != null) {
                when (input.code) {
                    SUCCESS -> return@map DataState(input.data ?: Topic())
                    codes.TOKEN_INVALID -> return@map DataState(DataState.STATE.TOKEN_INVALID)
                    else -> return@map DataState(
                        DataState.STATE.FETCH_FAILED,
                        input.message
                    )
                }
            }
            DataState(DataState.STATE.FETCH_FAILED)
        }
    }


    companion object {
        var instance: TopicWebSource? = null
        fun getInstance(context: Context): TopicWebSource {
            synchronized(TopicWebSource::class.java) {
                if (instance == null) {
                    instance = TopicWebSource(context.applicationContext)
                }
                return instance!!
            }
        }
    }

}