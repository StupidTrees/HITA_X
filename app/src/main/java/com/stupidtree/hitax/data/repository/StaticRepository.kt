package com.stupidtree.hitax.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.stupidtree.component.data.DataState
import com.stupidtree.hitax.data.source.web.StaticWebSource

class StaticRepository internal constructor(application: Application) {
    private val staticWebSource = StaticWebSource.getInstance(application)

    fun getAboutPage(): LiveData<DataState<String?>> {
        return staticWebSource.getAboutPage()
    }

    fun getUAPage(): LiveData<DataState<String?>> {
        return staticWebSource.getUserAgreementPage()
    }

    fun getPPPage(): LiveData<DataState<String?>> {
        return staticWebSource.getPrivacyPolicyPage()
    }

    companion object {
        @Volatile
        private var instance: StaticRepository? = null
        fun getInstance(application: Application): StaticRepository {
            synchronized(StaticRepository::class.java) {
                if (instance == null) instance = StaticRepository(application)
                return instance!!
            }
        }
    }
}