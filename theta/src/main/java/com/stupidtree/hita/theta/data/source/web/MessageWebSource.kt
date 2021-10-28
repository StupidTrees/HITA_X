package com.stupidtree.hita.theta.data.source.web

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.stupidtree.component.data.DataState
import com.stupidtree.component.web.BaseWebSource
import com.stupidtree.component.web.ApiResponse
import com.stupidtree.hita.theta.data.model.Message
import com.stupidtree.hita.theta.data.model.Topic
import com.stupidtree.hita.theta.data.source.web.service.MessageService
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

class MessageWebSource(context: Context) : BaseWebSource<MessageService>(
    context
) {
    override fun getServiceClass(): Class<MessageService> {
        return MessageService::class.java
    }

    fun getMessages(
        token: String,
        mode: String,
        pageSize: Int,
        pageNum: Int
    ): LiveData<DataState<List<Message>>> {
        return Transformations.map(
            service.getMessages(
                HttpUtils.getHeaderAuth(token), mode, pageSize, pageNum
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

    fun countUnread(
        token: String,
        mode: String
    ): LiveData<DataState<Int>> {
        return Transformations.map(
            service.countUnread(
                HttpUtils.getHeaderAuth(token), mode
            )
        ) { input ->
            if (input != null) {
                when (input.code) {
                    SUCCESS -> return@map DataState(input.data ?: 0)
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
        var instance: MessageWebSource? = null
        fun getInstance(context: Context): MessageWebSource {
            synchronized(MessageWebSource::class.java) {
                if (instance == null) {
                    instance = MessageWebSource(context.applicationContext)
                }
                return instance!!
            }
        }
    }

}