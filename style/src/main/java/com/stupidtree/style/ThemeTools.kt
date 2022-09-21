package com.stupidtree.style

import android.app.Activity
import android.content.Context
import android.util.Log

object ThemeTools {
    enum class MODE { DARK, LIGHT, FOLLOW }


    fun getThemeMode(context: Context): MODE {
        val pr = context.getSharedPreferences("theme", Context.MODE_PRIVATE)
        return when (pr.getString("mode", "follow")) {
            "dark" -> MODE.DARK
            "light" -> MODE.LIGHT
            else -> MODE.FOLLOW
        }
    }

    private fun setThemeMode(context: Context, mode: MODE) {
        val pr = context.getSharedPreferences("theme", Context.MODE_PRIVATE)
        val str = when (mode) {
            MODE.DARK -> "dark"
            MODE.LIGHT -> "light"
            else -> "follow"
        }
        pr.edit().putString("mode", str).apply()
    }

    fun switchTheme(activity:Activity){
        val newMode = when(getThemeMode(activity)){
            MODE.DARK->MODE.LIGHT
            MODE.LIGHT->MODE.FOLLOW
            MODE.FOLLOW->MODE.DARK
        }
        setThemeMode(activity,newMode)
        activity.recreate()
    }
}