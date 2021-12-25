package com.stupidtree.hitax.data.model.eas

import com.google.gson.Gson

/**
 * 成绩条目：每个对应一条成绩
 */
class CourseScoreItem{
    var finalScores: Int = 0
    var credits: Int = 0
    var hours: Int = 0
    var courseName: String? = null
    var courseCode: String? = null
    var courseProperty: String? = null
    var courseCategory: String? = null
    var schoolName: String? = null
    var assessMethod: String? = null
    var termName: String? = null

    override fun toString(): String {
        return Gson().toJson(this)
    }

}