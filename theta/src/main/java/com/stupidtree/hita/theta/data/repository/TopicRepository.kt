package com.stupidtree.hita.theta.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.stupidtree.component.data.DataState
import com.stupidtree.hita.theta.data.model.Topic
import com.stupidtree.hita.theta.data.source.web.TopicWebSource

class TopicRepository(val application: Application) {
    private val topicWebSource = TopicWebSource.getInstance(application)


    fun getTopics(
        token: String,
        mode: String,
        pageSize: Int,
        pageNum: Int,
        extra: String
    ): LiveData<DataState<List<Topic>>> {
        return topicWebSource.getTopics(token, mode, pageSize, pageNum, extra)
    }

    fun getTopic(
        token: String,
        id:String
    ): LiveData<DataState<Topic>> {
        return topicWebSource.getTopic(token,id)
    }

    companion object {
        @JvmStatic
        var instance: TopicRepository? = null
            private set

        fun getInstance(application: Application): TopicRepository {
            if (instance == null) {
                instance = TopicRepository(application)
            }
            return instance!!
        }

    }

}