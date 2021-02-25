package com.stupidtree.hitax.data.model.service

import com.google.gson.Gson
import java.io.Serializable

/**
 * 缓存在本地的此用户Model
 * 暂未和服务器返回数据格式匹配，需要适配函数
 */
class UserLocal : Serializable {
    //定义性别的枚举类型
    enum class GENDER {
        MALE, FEMALE,OTHER
    }

    var username //用户名
            : String? = null
    var id //用户id
            : String? = null
    var nickname //用户昵称
            : String? = null
    var signature //用户签名
            : String? = null
    var gender //用户性别
            : GENDER? = null
    var avatar //用户头像链接
            : String? = null

    var token //保存用户登陆状态的token（重要）
            : String? = null
    var publicKey: String? = null

    var studentId: String? = null
    var school: String? = null


    fun setGender(gender: String?) {
        this.gender = if (gender == "MALE") GENDER.MALE else GENDER.FEMALE
    }

    override fun toString(): String {
        return Gson().toJson(this)
    }

    fun isValid(): Boolean {
        return !publicKey.isNullOrEmpty() && !token.isNullOrEmpty()
    }
}