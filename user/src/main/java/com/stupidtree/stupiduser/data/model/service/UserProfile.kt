package com.stupidtree.stupiduser.data.model.service

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.stupidtree.stupiduser.data.model.service.UserLocal.GENDER

/**
 * 显示在用户资料页的用户资料Model
 * 和服务器返回数据匹配，无需适配函数
 */
@Entity(tableName = "profile")
class UserProfile {

    @PrimaryKey
    var id //用户id
            : String = ""
    var username //用户名
            : String? = null
    var nickname //昵称
            : String? = null
    var gender //性别
            : GENDER? = null
    var signature //签名
            : String? = null
    var avatar //头像
            : String? = null
    var school: String? = null

    var studentId: String? = null

    override fun toString(): String {
        return Gson().toJson(this)
    }
}