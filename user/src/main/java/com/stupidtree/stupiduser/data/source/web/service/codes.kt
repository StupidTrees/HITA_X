package com.stupidtree.stupiduser.data.source.web.service

/**
 * api请求返回码，和后端保持一致
 */
object codes {
    const val SUCCESS = 200
    const val USER_ALREADY_EXISTS = 301
    const val WRONG_USERNAME = 303
    const val WRONG_PASSWORD = 304
    const val TOKEN_INVALID = 2005
}