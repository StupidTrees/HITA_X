package com.stupidtree.hita.data.source.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.stupidtree.hita.data.model.timetable.EventItem
import com.stupidtree.hita.data.model.timetable.TermSubject
import javax.security.auth.Subject

@Dao
interface SubjectDao{

    @Query("SELECT * FROM subject WHERE timetableId is :timetableId AND name is :name")
    fun getSubjectByName(timetableId:String,name:String?):TermSubject?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveSubject(data: TermSubject)

}