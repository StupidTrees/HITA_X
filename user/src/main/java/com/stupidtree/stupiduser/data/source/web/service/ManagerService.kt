package com.stupidtree.stupiduser.data.source.web.service

import androidx.lifecycle.LiveData
import com.stupidtree.stupiduser.data.model.ApiResponse
import com.stupidtree.stupiduser.data.model.CheckUpdateResult
import com.stupidtree.stupiduser.data.model.UserLocal
import retrofit2.http.*

/**
 * 层次：Service
 * 用户网络服务
 */
interface ManagerService {
    /**
     * 检查更新
     */
    @GET("/manager/check_update")
    fun checkForUpdate(
        @Header("Authorization") token: String,
        @Query("versionCode") versionCode: Long,
        @Query("id") stuId: String,
        @Query("client") client: String,
    ): LiveData<ApiResponse<CheckUpdateResult>?>

}