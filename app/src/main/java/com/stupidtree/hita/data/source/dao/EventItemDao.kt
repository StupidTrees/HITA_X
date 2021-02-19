package com.stupidtree.hita.data.source.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.stupidtree.hita.data.model.timetable.EventItem
import com.stupidtree.hita.ui.timetable.subject.TeacherInfo

@Dao
interface EventItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveEvents(data: List<EventItem>)


    @Query("DELETE FROM events WHERE timetableId is :timetableId AND (type is 'CLASS' OR type is 'EXAM')")
    fun deleteCourseFromTimetable(timetableId: String)

    @Query("SELECT * FROM events WHERE `from` >= :fromT AND `from` <= :toT AND `to` >= :fromT AND `to` <= :toT")
    fun getEventsDuring(fromT: Long, toT: Long): LiveData<List<EventItem>>


    @Query("SELECT * FROM events WHERE subjectId is :subjectId")
    fun getClassesOfSubjectSync(subjectId: String): List<EventItem>


    @Query("SELECT * FROM events WHERE subjectId is :subjectId ORDER BY `from`")
    fun getClassesOfSubject(subjectId: String): LiveData<List<EventItem>>


    @Query("SELECT DISTINCT teacher,subject.name FROM events,subject WHERE events.timetableId is:timetableId  AND subject.timetableId is:timetableId  AND events.subjectId is subject.id AND events.teacher is NOT NULL")
    fun getTeachersOfTimetable(timetableId: String): LiveData<MutableList<TeacherInfo>>

    @Query("SELECT DISTINCT teacher FROM events WHERE subjectId is :subjectId AND timetableId is :timetableId AND  teacher is NOT NULL")
    fun getTeachersOfSubject(timetableId: String,subjectId: String): LiveData<List<String>>


    @Query("SELECT count(*) from events where subjectId is :subjectId")
    fun countClassesOfSubjectSync(subjectId: String):Int

    @Query("SELECT count(*) from events where subjectId is :subjectId and `to` < :ts")
    fun countClassesBeforeTimeOfSubjectSync(subjectId: String,ts:Long):Int
}