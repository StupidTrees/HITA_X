package com.stupidtree.hita.data.model.timetable

import androidx.room.ColumnInfo

class SubjectColor {
    @ColumnInfo(name = "id")
    var id: String = ""

    @ColumnInfo(name = "color")
    var color: Int = 0
}