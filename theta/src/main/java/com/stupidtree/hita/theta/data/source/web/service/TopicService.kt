package com.stupidtree.hita.theta.data.source.web.service

import androidx.lifecycle.LiveData
import com.stupidtree.component.web.ApiResponse
import com.stupidtree.hita.theta.data.model.Topic
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query


interface TopicService {
    @GET("/topic/gets")
    fun getTopics(
        @Header("Authorization") token: String,
        @Query("mode") mode: String,
        @Query("pageSize") pageSize: Int,
        @Query("pageNum") pageNum: Int,
        @Query("extra") extra: String
    ): LiveData<ApiResponse<List<Topic>>>

    @GET("/topic/get")
    fun getTopic(
        @Header("Authorization") token: String,
        @Query("topicId") topicId: String
    ): LiveData<ApiResponse<Topic>>
}