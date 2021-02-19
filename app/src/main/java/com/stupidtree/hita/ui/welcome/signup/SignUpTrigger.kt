package com.stupidtree.hita.ui.welcome.signup


import com.stupidtree.hita.data.model.service.UserLocal
import com.stupidtree.hita.ui.base.Trigger

class SignUpTrigger : Trigger() {
    var username: String? = null
        private set
    var password: String? = null
        private set
    private var gender: UserLocal.GENDER? = null
    var nickname: String? = null
        private set

    fun getGender(): String {
        return gender?.name.toString()
    }

    companion object {
        @JvmStatic
        fun getRequestState(username: String?, password: String?,
                            gender: UserLocal.GENDER?, nickname: String?
        ): SignUpTrigger {
            val ls = SignUpTrigger()
            ls.username = username
            ls.password = password
            ls.gender = gender
            ls.nickname = nickname
            ls.setActioning()
            return ls
        }
    }
}