package com.stupidtree.stupiduser.data.model

import androidx.annotation.StringRes

class SignUpResult {
    enum class STATES {
        SUCCESS, ERROR, REQUEST_FAILED, USER_EXISTS
    }

    var state: STATES? = null

    @get:StringRes
    var message = 0
    var userLocal: UserLocal? = null

    constructor()
    constructor(state: STATES?, message: Int) {
        this.state = state
        this.message = message
    }

    operator fun set(state: STATES?, @StringRes message: Int) {
        this.state = state
        this.message = message
    }

}