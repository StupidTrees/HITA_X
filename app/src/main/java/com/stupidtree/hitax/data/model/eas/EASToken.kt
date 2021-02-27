package com.stupidtree.hitax.data.model.eas

import java.util.*

class EASToken {

    var cookies = HashMap<String, String>()
    var username: String? = null
    var password: String? = null


    fun isLogin(): Boolean {
        return !cookies.isNullOrEmpty()
    }

    override fun toString(): String {
        return "EASToken(cookies=$cookies, username=$username, password=$password)"
    }


}