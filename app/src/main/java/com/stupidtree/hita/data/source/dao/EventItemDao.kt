package com.stupidtree.hita.data.source.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.stupidtree.hita.data.model.timetable.EventItem

@Dao
interface EventItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveEvents(data: List<EventItem>)


    @Query("DELETE FROM events WHERE timetableId is :timetableId AND (type is 'CLASS' OR type is 'EXAM')")
    fun deleteCourseFromTimetable(timetableId:String)

    @Query("SELECT * FROM events WHERE `from` >= :fromT AND `from` <= :toT AND `to` >= :fromT AND `to` <= :toT")
    fun getEventsDuring(fromT:Long, toT:Long):LiveData<List<EventItem>>
}