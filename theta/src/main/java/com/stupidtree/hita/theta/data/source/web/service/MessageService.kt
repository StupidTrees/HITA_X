package com.stupidtree.hita.theta.data.source.web.service

import androidx.lifecycle.LiveData
import com.stupidtree.component.web.ApiResponse
import com.stupidtree.hita.theta.data.model.Message
import com.stupidtree.hita.theta.data.model.Topic
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query


interface MessageService {
    @GET("/message/unread")
    fun countUnread(
        @Header("Authorization") token: String,
        @Query("mode") mode: String,
    ): LiveData<ApiResponse<Int>>

    @GET("/message/gets")
    fun getMessages(
        @Header("Authorization") token: String,
        @Query("mode") mode: String,
        @Query("pageSize") pageSize: Int,
        @Query("pageNum") pageNum: Int
    ): LiveData<ApiResponse<List<Message>>>
}