package com.stupidtree.hitax.ui.eas.classroom

import org.json.JSONObject
import java.io.Serializable

class ClassroomItem:Serializable {
    var name:String = ""
    var id:String = ""
    var capacity:Int = 0
    //是否阶梯教室
    var specialClassroom:String?=""

    var scheduleList = mutableListOf<JSONObject>()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ClassroomItem

        if (name != other.name) return false
        if (id != other.id) return false
        if (capacity != other.capacity) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + capacity
        return result
    }

    override fun toString(): String {
        return "ClassroomItem(name='$name', id='$id', capacity=$capacity, specialClassroom=$specialClassroom, scheduleList=$scheduleList)"
    }

}