package com.stupidtree.hitax.ui.eas.login

import com.stupidtree.component.data.Trigger

class LoginTrigger : Trigger() {
    lateinit var username: String
    lateinit var password: String
    var code: String? = null

    companion object {
        fun getActioning(username: String, password: String, code: String?): LoginTrigger {
            val r = LoginTrigger()
            r.username = username
            r.password = password
            r.code = code
            r.setActioning()
            return r
        }
    }
}