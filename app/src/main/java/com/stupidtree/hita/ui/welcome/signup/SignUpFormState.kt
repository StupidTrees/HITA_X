package com.stupidtree.hita.ui.welcome.signup

/**
 * 登录表单View的数据封装
 */
class SignUpFormState {
    var usernameError //用户名错误
            : Int? = null
        private set
    var passwordError //密码错误
            : Int? = null
        private set
    var passwordConfirmError //用户名错误
            : Int? = null
        private set
    var nicknameError //昵称错误
            : Int? = null
        private set
    var isFormValid = false
        private set

    constructor(usernameError: Int?, passwordError: Int?, passwordConfirmError: Int?, nicknameError: Int?) {
        this.usernameError = usernameError
        this.passwordError = passwordError
        this.passwordConfirmError = passwordConfirmError
        this.nicknameError = nicknameError
    }

    constructor(formValid: Boolean) {
        if (formValid) {
            usernameError = null
            passwordError = null
            passwordConfirmError = null
            nicknameError = null
        }
        isFormValid = formValid
    }

}