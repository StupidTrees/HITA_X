package com.stupidtree.hitax.data.model.timetable

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.stupidtree.hitax.utils.ColorTools
import java.sql.Timestamp
import java.util.*

@Entity(tableName = "subject")
class TermSubject{
    //必修-考试，必修-考察，选修-专选，选修-任选，慕课
    enum class TYPE {
        COM_A, COM_B, OPT_A, OPT_B, MOOC,TAG
    }

    @PrimaryKey
    var id: String = UUID.randomUUID().toString()
    var name //名称
            : String = ""
    var timetableId //所属课表的id
            : String = ""
    var type //课程类型
            : TYPE = TYPE.COM_A
    var field: String? = null//课程领域（人文艺术、科学技术）
    var credit //学分
            = 0f
    var school //开课院系
            : String? = null
    var countInSPA //是否计入平均学分绩
            = false
    var code //适配教务的课程代码
            : String? = null
    var key //适配教务的课程标识
            : String? = null
    var createdAt //创建时间
            : Timestamp = Timestamp(System.currentTimeMillis())
    var color:Int = ColorTools.randomColorMaterial()

}