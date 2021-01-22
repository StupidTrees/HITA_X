package com.stupidtree.hita.data.model.timetable

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Timestamp
import java.util.*
import kotlin.math.abs

@Entity(tableName = "events")
class EventItem {
    enum class TYPE {
        CLASS, EXAM, OTHER, TAG
    }

    @PrimaryKey
    var id: String = UUID.randomUUID().toString()
    var type: TYPE = TYPE.CLASS
    var name //名称
            : String = "NULL"
    var place //地点
            : String? = ""
    var teacher //教师
            : String? = ""
    var subjectId //科目id
            : String = ""
    var timetableId //课表的id
            : String = ""
    var from //开始时间
            : Timestamp = Timestamp(0)
    var to //结束时间
            : Timestamp = Timestamp(0)

    @ColumnInfo(name = "created_at")
    var createdAt //创建时间
            : Timestamp = Timestamp(System.currentTimeMillis())

    override fun toString(): String {
        return "EventItem(id='$id', type=$type, name='$name', place=$place, teacher=$teacher, subjectId='$subjectId', timetableId='$timetableId', from=$from, to=$to, createdAt=$createdAt"
    }


    /**
     * 获取fromTime和当前时间的距离（分钟）
     */
    fun getFromTimeDistance(): Int {
        return (abs(from.time - System.currentTimeMillis()) / 60000).toInt()
    }

    /**
     * 该事件的时间范围是否包括某时间戳
     */
    fun containsTimeStamp(ts: Long): Boolean {
        val timestamp = Timestamp(ts)
        return from.before(timestamp) && to.after(timestamp)
    }

    /**
     * 在某时间戳之后发生
     */
    fun happensAfterTimeStamp(ts:Long):Boolean{
        val timestamp = Timestamp(ts)
        return from.after(timestamp)
    }

    /**
     * 计算某时间戳在本事件中的进度
     */
    fun getProgress(ts:Long):Float{
        val timestamp = Timestamp(ts)
        return when {
            timestamp.before(from) -> {
                0f
            }
            timestamp.after(to) -> {
                1f
            }
            else -> {
                ((ts-from.time).toFloat()/(to.time-from.time).toFloat())
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EventItem

        if (id != other.id) return false
        if (type != other.type) return false
        if (name != other.name) return false
        if (place != other.place) return false
        if (teacher != other.teacher) return false
        if (subjectId != other.subjectId) return false
        if (timetableId != other.timetableId) return false
        if (from != other.from) return false
        if (to != other.to) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + (place?.hashCode() ?: 0)
        result = 31 * result + (teacher?.hashCode() ?: 0)
        result = 31 * result + subjectId.hashCode()
        result = 31 * result + timetableId.hashCode()
        result = 31 * result + from.hashCode()
        result = 31 * result + to.hashCode()
        return result
    }

}