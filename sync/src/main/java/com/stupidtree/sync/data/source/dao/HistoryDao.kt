package com.stupidtree.sync.data.source.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.stupidtree.sync.data.model.History

@Dao
interface HistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addHistory(data: History)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addHistories(data: List<History>)

    @Query("select id from history where uid is :uid order by -id limit 1 ")
    fun getLatestId(uid: String): Long?

    @Query("select * from history where uid is :uid and id > :latestId")
    fun getHistoryAfterSync(uid: String, latestId: Long): List<History>


    @Query("delete from history")
    fun clear()

    @Query("delete from history where uid is :uid and id <> :id")
    fun clearBut(uid:String,id:String)
}