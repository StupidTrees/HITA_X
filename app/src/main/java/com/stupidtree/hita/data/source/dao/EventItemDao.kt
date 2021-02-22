package com.stupidtree.hita.data.source.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.stupidtree.hita.data.model.timetable.EventItem
import com.stupidtree.hita.ui.timetable.subject.TeacherInfo
import java.sql.Timestamp

@Dao
interface EventItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveEvents(data: List<EventItem>)


    @Query("DELETE FROM events WHERE timetableId is :timetableId AND (type is 'CLASS' OR type is 'EXAM')")
    fun deleteCourseFromTimetable(timetableId: String)

    @Query("SELECT * FROM events WHERE `from` >= :fromT AND `from` <= :toT AND `to` >= :fromT AND `to` <= :toT")
    fun getEventsDuring(fromT: Long, toT: Long): LiveData<List<EventItem>>

    @Query("SELECT * FROM events WHERE `from` >= :fromT AND `from` <= :toT AND `to` >= :fromT AND `to` <= :toT")
    fun getEventsDurin(fromT: Long, toT: Long):LiveData<List<EventItem>>


    @Query("SELECT * FROM events WHERE subjectId is :subjectId")
    fun getClassesOfSubjectSync(subjectId: String): List<EventItem>


    @Query("SELECT * FROM events WHERE subjectId is :subjectId ORDER BY `from`")
    fun getClassesOfSubject(subjectId: String): LiveData<List<EventItem>>


    @Query("SELECT DISTINCT teacher,subject.name FROM events,subject WHERE events.timetableId is:timetableId  AND subject.timetableId is:timetableId  AND events.subjectId is subject.id AND events.teacher is NOT NULL")
    fun getTeachersOfTimetable(timetableId: String): LiveData<MutableList<TeacherInfo>>

    @Query("SELECT DISTINCT teacher FROM events WHERE subjectId is :subjectId AND timetableId is :timetableId AND  teacher is NOT NULL")
    fun getTeachersOfSubject(timetableId: String, subjectId: String): LiveData<List<String>>


    @Query("SELECT count(*) from events where subjectId is :subjectId")
    fun countClassesOfSubjectSync(subjectId: String): Int

    @Query("SELECT count(*) from events where subjectId is :subjectId and `to` < :ts")
    fun countClassesBeforeTimeOfSubjectSync(subjectId: String, ts: Long): Int

    @Query("DELETE from events where timetableId in (:ids)")
    fun deleteEventsFromTimetablesSync(ids: List<String>)

    @Query("SELECT * from events where type is 'CLASS' and timetableId is :timetableId and fromNumber is :fromNumber")
    fun getClassAtFromNumberSync(
        timetableId: String,
        fromNumber: Int
    ): List<EventItem>

    @Query("SELECT * from events where type is 'CLASS' and timetableId is :timetableId and fromNumber+lastNumber-1 is :toNumber")
    fun getClassAtToNumberSync(
        timetableId: String,
        toNumber: Int
    ): List<EventItem>

}