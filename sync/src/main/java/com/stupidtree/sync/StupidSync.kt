package com.stupidtree.sync

import android.content.Context
import android.os.Looper
import android.util.Log
import androidx.annotation.WorkerThread
import com.google.gson.JsonObject
import com.stupidtree.sync.data.model.History
import com.stupidtree.sync.data.model.SyncResult
import com.stupidtree.sync.data.source.dao.HistoryDao
import com.stupidtree.sync.data.source.web.SyncWebSource
import com.stupidtree.sync.util.Snowflake
import java.util.concurrent.Executors

object StupidSync {

    var historyDao: HistoryDao? = null
    var syncWebSource:SyncWebSource?=null
    private var uid: String? = null
    private val snowflakeIdWorker = Snowflake(1, 1)
    private var executor = Executors.newSingleThreadExecutor()
    var pushDelegate: PushDelegate? = null

    interface PushDelegate {
        @WorkerThread
        fun getDataForIds(key: String, ids: List<String>): List<JsonObject>

        @WorkerThread
        fun saveData(key: String, ids: List<String>, data: List<String>)

        @WorkerThread
        fun deleteData(key: String, ids: List<String>)
    }


    fun init(context: Context, pushDelegate: PushDelegate) {
        this.pushDelegate = pushDelegate
        syncWebSource = SyncWebSource.getInstance(context)
        historyDao = HistoryDatabase.getDatabase(context).historyDao()
    }

    fun setUID(UID: String?) {
        uid = UID
    }


    @WorkerThread
    fun putHistorySync(table: String, action: History.ACTION, ids: List<String>) {
        historyDao?.let { dao ->
            uid?.let { uid ->
                dao.addHistory(History().also {
                    it.id = snowflakeIdWorker.nextId()
                    it.uid = uid
                    it.table = table
                    it.action = action
                    it.ids = ids
                })
            }

        } ?: kotlin.run {
            Log.e("SSYNC", "NOT INITIALIZED")
        }
    }


    interface SyncCallback {
        fun onSuccess()
        fun onFailed(e: java.lang.Exception)
    }

    fun sync(callback: SyncCallback) {
        if (uid == null || historyDao == null) {
            callback.onFailed(java.lang.Exception("uid is empty"))
            return
        }
        executor.execute {
            try {
                val latestIdLocal = historyDao?.getLatestId(uid!!)
                val resp = syncWebSource?.sync(uid!!, latestIdLocal ?: 0)?.execute()
                resp?.body()?.data?.let {
                    if (it.action == SyncResult.ACTION.PUSH) {
                        pushSync(it.latestId)
                    } else if (it.action == SyncResult.ACTION.PULL) {
                        val histories = it.getHistories()
                        val dataMap = it.getDataMap()
                        for (h in histories) {
                            if (h.action == History.ACTION.REMOVE) {
                                pushDelegate?.deleteData(h.table, h.ids)
                            } else {
                                dataMap[h.table]?.let { dm ->
                                    pushDelegate?.saveData(h.table, h.ids, dm)
                                }
                            }
                        }
                        historyDao?.addHistories(histories)
                    }
                }
                android.os.Handler(Looper.getMainLooper()).post {
                    callback.onSuccess()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                android.os.Handler(Looper.getMainLooper()).post {
                    callback.onFailed(e)
                }

            }
        }
    }

    @WorkerThread
    fun pushSync(latestIdCloud: Long) {
        try {
            if (uid == null || historyDao == null) return
            val list = historyDao!!.getHistoryAfterSync(uid!!, latestIdCloud)
            val requiredId = mutableMapOf<String, MutableList<String>>()
            val data = mutableMapOf<String, List<JsonObject>>()
            for (h in list) {
                if (requiredId[h.table] == null) {
                    requiredId[h.table] = mutableListOf()
                }
                if (h.action == History.ACTION.REMOVE) {
                    requiredId[h.table]?.removeAll(h.ids)
                } else {
                    requiredId[h.table]?.addAll(h.ids)
                }
            }
            for ((k, v) in requiredId) {
                data[k] = pushDelegate?.getDataForIds(k, v) ?: listOf()
            }
            //Log.e("data", data.toString())
            syncWebSource?.push(uid!!, list, data)?.execute()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}