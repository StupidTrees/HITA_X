package com.stupidtree.hitax.data.source.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.stupidtree.hitax.data.model.timetable.Timetable

@Dao
interface TimetableDao {


    /**
     * 根据教务代码查找课表
     */
    @Query("SELECT * FROM timetable WHERE code is :easCode")
    fun getTimetableByEASCodeSync(easCode: String): Timetable?

    @Query("SELECT * FROM timetable WHERE code is :easCode")
    fun getTimetableByEASCode(easCode: String): LiveData<Timetable?>

    @Query("SELECT * FROM timetable order by -startTime")
    fun getTimetables(): LiveData<List<Timetable>>

    @Query("SELECT * FROM timetable WHERE id is :id")
    fun getTimetableById(id: String): LiveData<Timetable>

    /**
     *  获取所有使用默认名字的课表名字
     */
    @Query("SELECT name from timetable where name like :defaultName")
    fun getTimetableNamesWithDefaultSync(defaultName: String): List<String>

    /**
     * 获得离某时间戳最近的时间表
     */
    @Query("SELECT * from timetable where (:ts-startTime in (select min(:ts-startTime) from timetable where :ts>startTime)) limit 1")
    fun getTimetableClosestToTimestamp(ts: Long): LiveData<Timetable?>

    @Query("SELECT * from timetable where (:ts-startTime in (select min(:ts-startTime) from timetable where :ts>startTime)) limit 1")
    fun getTimetableClosestToTimestampSync(ts: Long): Timetable?


    @Query("select count(*) from timetable")
    fun geeTimetableCount(): LiveData<Int>



    /**
     * 保存课表
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveTimetableSync(data: Timetable)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveTimetablesSync(data: List<Timetable>)

    @Delete
    fun deleteTimetablesSync(timetables: List<Timetable>)

    @Query("delete from timetable where id in (:ids)")
    fun deleteTimetablesInIdsSync(ids: List<String>)


    @Query("SELECT * FROM timetable WHERE id in (:ids)")
    fun getTimetablesInIdsSync(ids: List<String>): List<Timetable>



    @Query("delete from timetable")
    fun clear()
}