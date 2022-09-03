package com.stupidtree.hitax.data.model.eas

import java.util.*

class EASToken {

    enum class TYPE { UNDERGRAD, GRAD }

    var cookies = HashMap<String, String>()
    var username: String? = null
    var password: String? = null
    var name:String? = null
    var stutype: TYPE = TYPE.UNDERGRAD // 培养类型，1本科生，其他研究生
    var picture:String?=null //照片
    var id:String?=null //学生id
    var stuId:String?=null //学号
    var school:String?=null // 学院
    var major:String?=null //专业
    var grade:String?=null //年级
    var sfxsx:String?=null //不知道什么
    var email:String?=null //邮箱
    var phone:String?=null //电话

    fun getStudentType(): String {
        return if (stutype == TYPE.UNDERGRAD) "1" else "2"
    }

    fun isLogin(): Boolean {
        return cookies.isNotEmpty()
    }

    override fun toString(): String {
        return "EASToken(cookies=$cookies, username=$username, password=$password, name=$name, stutype=${getStudentType()}, picture=$picture, id=$id, stuId=$stuId, school=$school, major=$major, grade=$grade, sfxsx=$sfxsx, email=$email, phone=$phone)"
    }


}