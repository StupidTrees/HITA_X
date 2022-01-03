package com.stupidtree.hitax.data.model.eas

import com.google.gson.Gson

/**
 * 考试条目：每个对应一条考试
 */
class ExamItem {
    var courseName:String? = null
    var examDate:String? = null
    var examTime:String? = null
    var examType:String? = null// 期中、期末
    var examLocation:String? = null
    var termName:String? = null
    var campusName:String? = null// 深圳校区

    override fun toString(): String {
        return Gson().toJson(this)
    }
}