package com.stupidtree.hitax.utils

object HttpUtils {
    fun getHeaderAuth(token: String): String {
        return "Bearer $token"
    }
}