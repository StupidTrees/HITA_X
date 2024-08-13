package com.stupidtree.stupiduser.data.source.web

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.stupidtree.component.data.DataState
import com.stupidtree.component.web.BaseWebSource
import com.stupidtree.hitax.data.source.web.service.UserService
import com.stupidtree.stupiduser.R
import com.stupidtree.stupiduser.data.model.*
import com.stupidtree.stupiduser.data.source.web.service.ManagerService
import com.stupidtree.stupiduser.data.source.web.service.codes
import com.stupidtree.stupiduser.data.source.web.service.codes.SUCCESS
import com.stupidtree.stupiduser.data.source.web.service.codes.USER_ALREADY_EXISTS
import com.stupidtree.stupiduser.data.source.web.service.codes.WRONG_PASSWORD
import com.stupidtree.stupiduser.data.source.web.service.codes.WRONG_USERNAME
import com.stupidtree.stupiduser.util.HttpUtils

/**
 * 层次：DataSource
 * 用户的数据源
 * 类型：网络数据
 * 数据：异步读，异步写
 */
class ManagerWebSource(context: Context) : BaseWebSource<ManagerService>(
    context
) {
    override fun getServiceClass(): Class<ManagerService> {
        return  ManagerService::class.java
    }
    /**
     * 检查更新
     * @param token 用户令牌
     * @return 是否更新
     */
    fun checkUpdate(token: String,versionCode:Long,id: String?): LiveData<DataState<CheckUpdateResult>> {
        return service.checkForUpdate(HttpUtils.getHeaderAuth(token),versionCode,id?:"","android").map{ input ->
            if (input != null) {
                when (input.code) {
                    SUCCESS -> input.data?.let { return@map DataState(it) }
                    codes.TOKEN_INVALID -> return@map DataState(DataState.STATE.TOKEN_INVALID)
                    else -> return@map DataState(DataState.STATE.FETCH_FAILED)
                }
            }
            DataState(DataState.STATE.FETCH_FAILED)
        }
    }

    companion object {
        var instance: ManagerWebSource? = null
        fun getInstance(context: Context): ManagerWebSource {
            synchronized(ManagerWebSource::class.java) {
                if (instance == null) {
                    instance = ManagerWebSource(context.applicationContext)
                }
                return instance!!
            }
        }
    }
}