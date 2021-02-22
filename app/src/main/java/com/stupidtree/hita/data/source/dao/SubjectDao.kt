package com.stupidtree.hita.data.source.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.stupidtree.hita.data.model.timetable.EventItem
import com.stupidtree.hita.data.model.timetable.TermSubject
import javax.security.auth.Subject

@Dao
interface SubjectDao{

    @Query("SELECT * FROM subject WHERE id is :id")
    fun getSubjectById(id:String):LiveData<TermSubject>

    @Query("SELECT * FROM subject WHERE timetableId is :timetableId AND name is :name")
    fun getSubjectByName(timetableId:String,name:String?):TermSubject?

    @Query("SELECT * FROM subject WHERE timetableId is :timetableId")
    fun getSubjects(timetableId:String):List<TermSubject>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveSubjectSync(data: TermSubject)

    @Query("DELETE from subject where timetableId in (:ids)")
    fun deleteSubjectsFromTimetablesSync(ids:List<String>)

}