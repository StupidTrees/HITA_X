package com.stupidtree.stupiduser.util

object HttpUtils {
    fun getHeaderAuth(token: String): String {
        return "Bearer $token"
    }
}