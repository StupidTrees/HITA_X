package com.stupidtree.hita.utils

object HttpUtils {
    fun getHeaderAuth(token: String): String {
        return "Bearer $token"
    }
}