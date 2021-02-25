package com.stupidtree.hitax.ui.timetable.detail

import androidx.room.ColumnInfo

class TeacherInfo {
    @ColumnInfo(name = "teacher")
    var name: String? = ""

    @ColumnInfo(name = "name")
    var subjectName: String? = ""
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TeacherInfo

        if (name != other.name) return false
        if (subjectName != other.subjectName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name?.hashCode() ?: 0
        result = 31 * result + (subjectName?.hashCode() ?: 0)
        return result
    }


}