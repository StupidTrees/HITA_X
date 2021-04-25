package com.stupidtree.hita.theta.data.source.web.service

import androidx.lifecycle.LiveData
import com.stupidtree.component.web.ApiResponse
import com.stupidtree.hita.theta.data.model.Article
import com.stupidtree.hita.theta.data.model.LikeResult
import com.stupidtree.stupiduser.data.model.UserProfile
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*
import java.util.HashMap

interface ArticleService {

    @FormUrlEncoded
    @POST("/article/create")
    fun postArticle(
        @Header("Authorization") token: String,
        @Field("content") content: String,
        @Field("repostId") repostId: String?
    ): LiveData<ApiResponse<Boolean>?>

    /**
     * 上传头像
     * @param file 头像体
     * @param token 登录状态的token
     * @return 返回操作结果和文件名
     */
    @Multipart
    @POST("/article/create_images")
    fun postArticle(
        @PartMap params: HashMap<String, RequestBody>,
        @Header("Authorization") token: String?,
        @Query("content") content: String,
        @Query("repostId") repostId: String?
    ): LiveData<ApiResponse<Boolean>>


    @GET("/article/gets")
    fun getArticles(
        @Header("Authorization") token: String,
        @Query("mode") mode: String,
        @Query("beforeTime") beforeTime: Long,
        @Query("afterTime") afterTime: Long,
        @Query("pageSize") pageSize: Int,
        @Query("extra") extra: String
    ): LiveData<ApiResponse<List<Article>>>



    @GET("/article/get")
    fun getArticleInfo(
        @Header("Authorization") token: String,
        @Query("articleId") fromId: String?,
        @Query("digOrigin") digOrigin: Boolean
    ): LiveData<ApiResponse<Article>>

    @GET("/article/get")
    fun getArticleInfoCall(
        @Header("Authorization") token: String,
        @Query("articleId") fromId: String?,
        @Query("digOrigin") digOrigin: Boolean
    ): Call<ApiResponse<Article>>

    @FormUrlEncoded
    @POST("/article/like")
    fun likeOrUnlike(
        @Header("Authorization") token: String,
        @Field("articleId") articleId: String?,
        @Field("like") like: Boolean
    ): Call<ApiResponse<LikeResult>>

    @FormUrlEncoded
    @POST("/article/delete")
    fun delete(
        @Header("Authorization") token: String,
        @Field("articleId") articleId: String
    ): LiveData<ApiResponse<Any>>



    @FormUrlEncoded
    @POST("/article/like")
    fun likeOrUnlikeLive(
        @Header("Authorization") token: String,
        @Field("articleId") articleId: String?,
        @Field("like") like: Boolean
    ): LiveData<ApiResponse<LikeResult>>
}