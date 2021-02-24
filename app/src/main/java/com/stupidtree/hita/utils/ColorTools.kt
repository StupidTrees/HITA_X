package com.stupidtree.hita.utils

import android.graphics.Color
import java.util.*

object ColorTools {
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
    
    
    fun changeAlpha(color:Int,alpha:Float):Int{
        return Color.argb(
                (255*alpha).toInt(),
                Color.red(color),
                Color.green(color),
                Color.blue(color)
        )
    }
    
    
}