package com.stupidtree.hita.theta.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.stupidtree.component.data.DataState
import com.stupidtree.hita.theta.data.model.Message
import com.stupidtree.hita.theta.data.source.web.MessageWebSource

class MessageRepository(val application: Application) {
    private val messageWebSource = MessageWebSource.getInstance(application)


    fun getMessages(
        token: String,
        mode: String,
        pageSize: Int,
        pageNum: Int
    ): LiveData<DataState<List<Message>>> {
        return messageWebSource.getMessages(token, mode, pageSize, pageNum)
    }

    fun countUnread(
        token: String,
        mode: String
    ): LiveData<DataState<Int>> {
        return messageWebSource.countUnread(token, mode)
    }

    companion object {
        @JvmStatic
        var instance: MessageRepository? = null
            private set

        fun getInstance(application: Application): MessageRepository {
            if (instance == null) {
                instance = MessageRepository(application)
            }
            return instance!!
        }

    }

}