package com.stupidtree.stupiduser.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.stupidtree.component.data.DataState
import com.stupidtree.stupiduser.data.model.CheckUpdateResult
import com.stupidtree.stupiduser.data.source.web.ManagerWebSource

/**
 * Repository层：用户资料页面的Repository
 */
class ManagerRepository(application: Application) {
    private val managerWebSource = ManagerWebSource.getInstance(application)



    fun checkUpdate(token: String, versionCode:Long): LiveData<DataState<CheckUpdateResult>> {
        return managerWebSource.checkUpdate(token,versionCode)
    }


    companion object {
        @JvmStatic
        var instance: ManagerRepository? = null
            private set

        fun getInstance(application: Application): ManagerRepository {
            if (instance == null) {
                instance = ManagerRepository(application)
            }
            return instance!!
        }

    }

}