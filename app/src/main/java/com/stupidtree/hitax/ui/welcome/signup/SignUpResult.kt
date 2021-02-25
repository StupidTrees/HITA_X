package com.stupidtree.hitax.ui.welcome.signup

import androidx.annotation.StringRes
import com.stupidtree.hitax.data.model.service.UserLocal

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