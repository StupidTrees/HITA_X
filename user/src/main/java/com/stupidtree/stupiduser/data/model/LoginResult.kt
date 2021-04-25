package com.stupidtree.stupiduser.data.model

import androidx.annotation.StringRes
import com.google.gson.Gson

/**
 * 封装了登录结果，预计要弃用
 */
class LoginResult {
    enum class STATES {
        SUCCESS, WRONG_USERNAME, WRONG_PASSWORD, ERROR, REQUEST_FAILED
    }

    var state: STATES? = null

    @get:StringRes
    var message = 0
    var userLocal: UserLocal? = null

    constructor() {}
    constructor(state: STATES?, message: Int) {
        this.state = state
        this.message = message
    }

    operator fun set(state: STATES?, @StringRes message: Int) {
        this.state = state
        this.message = message
    }

    override fun toString(): String {
        return Gson().toJson(this)
    }
}