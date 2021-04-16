package com.stupidtree.sync.data.source.web.service

import com.stupidtree.component.web.ApiResponse
import com.stupidtree.sync.data.model.SyncResult
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST


interface SyncService {

    @FormUrlEncoded
    @POST("/sync/sync")
    fun sync(
        @Field("uid") uid: String?,
        @Field("latestId") latestId: Long
    ): Call<ApiResponse<SyncResult>?>

    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST("/sync/push")
    fun push(
        @Field("uid") uid: String,
        @Field("history") history: String,
        @Field("data") data: String,
    ): Call<ApiResponse<Boolean>?>

}