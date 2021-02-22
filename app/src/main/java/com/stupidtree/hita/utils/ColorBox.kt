package com.stupidtree.hita.utils

import android.content.SharedPreferences
import android.graphics.Color
import java.util.*

object ColorBox {
    var colors_material = arrayOf(
        "#ef5350",
        "#ec407a",
        "#9c27b0",
        "#7e57c2",
        "#7c4dff",
        "#3f51b5",
        "#536dfe",
        "#2196f3",
        "#26c6da",
        "#009688",
        "#4caf50",
        "#fdd835"
    )
    fun randomColorMaterial(): Int {
        val random = Random()
        return Color.parseColor(colors_material[random.nextInt(colors_material.size - 1)])
    }

    fun getSubjectColor(SP: SharedPreferences, subjectName: String?): Int {
        var color = SP.getInt("color:$subjectName", -1)
        if (color == -1) {
            color = randomColorMaterial()
            SP.edit().putInt("color:$subjectName", color).apply()
        }
        return color
    }

    fun changeSubjectColor(SP: SharedPreferences, subjectName: String, color: Int) {
        SP.edit().putInt("color:$subjectName", color).apply()
    }
}