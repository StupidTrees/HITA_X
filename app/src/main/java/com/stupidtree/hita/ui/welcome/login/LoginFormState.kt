package com.stupidtree.hita.ui.welcome.login

/**
 * 登录表单View的数据封装
 */
class LoginFormState {
    var usernameError //用户名错误文本提示
            : Int?
        private set
    var passwordError //密码错误提示
            : Int?
        private set
    var isDataValid: Boolean
        private set

    constructor(usernameError: Int?, passwordError: Int?) {
        this.usernameError = usernameError
        this.passwordError = passwordError
        isDataValid = false
    }

    constructor(isDataValid: Boolean) {
        usernameError = null
        passwordError = null
        this.isDataValid = isDataValid
    }

}