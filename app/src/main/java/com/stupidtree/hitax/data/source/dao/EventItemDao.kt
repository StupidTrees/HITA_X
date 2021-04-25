package com.stupidtree.hitax.data.source.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.stupidtree.hitax.data.model.timetable.EventItem
import com.stupidtree.hitax.ui.timetable.detail.TeacherInfo

@Dao
interface EventItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveEvents(data: List<EventItem>)


    @Query("DELETE FROM events WHERE timetableId is :timetableId AND (type is 'CLASS' OR type is 'EXAM')")
    fun deleteCourseFromTimetable(timetableId: String)

    @Query("SELECT * FROM events WHERE `from` >= :fromT AND `from` <= :toT AND `to` >= :fromT AND `to` <= :toT")
    fun getEventsDuring(fromT: Long, toT: Long): LiveData<List<EventItem>>

    @Query("SELECT * FROM events WHERE `from` >= :fromT AND `from` <= :toT AND `to` >= :fromT AND `to` <= :toT")
    fun getEventsDurin(fromT: Long, toT: Long): LiveData<List<EventItem>>


    @Query("SELECT * FROM events WHERE subjectId is :subjectId")
    fun getClassesOfSubjectSync(subjectId: String): List<EventItem>


    @Query("SELECT * FROM events WHERE subjectId is :subjectId ORDER BY `from`")
    fun getClassesOfSubject(subjectId: String): LiveData<List<EventItem>>


    @Query("SELECT DISTINCT teacher,subject.name FROM events,subject WHERE events.timetableId is:timetableId  AND subject.timetableId is:timetableId  AND events.subjectId is subject.id AND events.teacher is NOT NULL")
    fun getTeachersOfTimetable(timetableId: String): LiveData<MutableList<TeacherInfo>>

    @Query("SELECT DISTINCT teacher FROM events WHERE subjectId is :subjectId AND timetableId is :timetableId AND  teacher is NOT NULL")
    fun getTeachersOfSubject(timetableId: String, subjectId: String): LiveData<List<String>>


    @Query("SELECT count(*) from events where subjectId is :subjectId")
    fun countClassesOfSubject(subjectId: String): LiveData<Int>

    @Query("SELECT count(*) from events where subjectId is :subjectId and `to` < :ts")
    fun countClassesBeforeTimeOfSubject(subjectId: String, ts: Long): LiveData<Int>


    @Query("DELETE from events where timetableId in (:ids)")
    fun deleteEventsFromTimetablesSync(ids: List<String>)

    @Query("select id from events where timetableId in (:ids)")
    fun getEventIdsFromTimetablesSync(ids: List<String>):List<String>

    @Query("select * from events where id in (:ids)")
    fun getEventInIdsSync(ids: List<String>):List<EventItem>

    @Query("select * from events where timetableId is :timetableId")
    fun getEventsOfTimetableSync(timetableId: String):List<EventItem>


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


    @Query("DELETE from events where subjectId in (:ids)")
    fun deleteEventsFromSubjectsSync(ids: List<String>)

    @Query("delete from events where id in (:ids)")
    fun deleteEventsInIdsSync(ids: List<String>)

    /**
     * 将某课表的所有课程时间加上offset
     */
    @Query("update events set `from` = (`from`+:offset) , `to` = (`to` + :offset) where  timetableId is :timetableId and type is 'CLASS'")
    fun updateClassesAddOffset(timetableId: String, offset: Long)


    @Query("delete from events")
    fun clear()
}