package com.stupidtree.hita.theta.data.source.web.service

import androidx.lifecycle.LiveData
import com.stupidtree.component.web.ApiResponse
import com.stupidtree.hita.theta.data.model.Article
import com.stupidtree.hita.theta.data.model.Comment
import com.stupidtree.hita.theta.data.model.LikeResult
import retrofit2.Call
import retrofit2.http.*

interface CommentService {

    @FormUrlEncoded
    @POST("/comment/create")
    fun postComment(
        @Header("Authorization") token: String,
        @Field("content") content: String,
        @Field("articleId") articleId: String,
        @Field("replyId") toCommentId: String?,
        @Field("receiverId") toUserId: String,
        @Field("contextId") contextId:String?
    ): LiveData<ApiResponse<Boolean>?>


    @GET("/comment/article")
    fun getCommentsOfArticle(
        @Header("Authorization") token: String,
        @Query("articleId") articleId: String,
        @Query("pageSize") pageSize: Int,
        @Query("pageNum") pageNum: Int
    ): LiveData<ApiResponse<List<Comment>>>


    @GET("/comment/reply")
    fun getCommentsOfComment(
        @Header("Authorization") token: String,
        @Query("commentId") articleId: String,
        @Query("pageSize") pageSize: Int,
        @Query("pageNum") pageNum: Int
    ): LiveData<ApiResponse<List<Comment>>>

    @GET("/comment/get")
    fun getCommentInfo(
        @Header("Authorization") token: String,
        @Query("commentId") articleId: String
    ): LiveData<ApiResponse<Comment>>

    @FormUrlEncoded
    @POST("/comment/like")
    fun likeOrUnlike(
        @Header("Authorization") token: String,
        @Field("commentId") commentId: String,
        @Field("like") like: Boolean
    ): Call<ApiResponse<LikeResult>>

    @FormUrlEncoded
    @POST("/comment/like")
    fun likeOrUnlikeLive(
        @Header("Authorization") token: String,
        @Field("commentId") articleId: String?,
        @Field("like") like: Boolean
    ): LiveData<ApiResponse<LikeResult>>
}