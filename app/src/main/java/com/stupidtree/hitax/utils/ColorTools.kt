package com.stupidtree.hitax.utils

import android.graphics.Color

object ColorTools {
    private const val TIME_INTERVAL = 1000 * 60.toLong()
    var lastTimestamp: Long = 0
    var colors_material = arrayOf(
        "#ec407a",
        "#FF9E00",
        "#7c4dff",
        "#536dfe",
        "#2196f3",
        "#26c6da",
        "#009688",
        "#7cba59",
        "#E96D71",
    )
    var colorsQueue = mutableListOf<String>()

    fun randomColorMaterial(): Int {
        //超过时间，重置队列
        if (colorsQueue.isEmpty() || lastTimestamp + TIME_INTERVAL < System.currentTimeMillis()) {
            colorsQueue.clear()
            colorsQueue.addAll(colors_material)
            colorsQueue.shuffle()
        }
        lastTimestamp = System.currentTimeMillis()
        return Color.parseColor(colorsQueue.removeAt(0))
    }


    fun changeAlpha(color: Int, alpha: Float): Int {
        return Color.argb(
            (255 * alpha).toInt(),
            Color.red(color),
            Color.green(color),
            Color.blue(color)
        )
    }


}