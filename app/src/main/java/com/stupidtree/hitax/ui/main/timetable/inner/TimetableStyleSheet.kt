package com.stupidtree.hitax.ui.main.timetable.inner

import android.graphics.Color
import android.view.Gravity
import com.stupidtree.hitax.data.model.timetable.TimeInDay

class TimetableStyleSheet {
    var isColorEnabled: Boolean = true
    var isFadeEnabled: Boolean = true
    var cardTitleColor: String = "white"
    var subTitleColor: String = "white"
    var iconColor: String = "white"
    var isBoldText: Boolean = true
    var drawBGLine: Boolean = true
    var cardIconEnabled: Boolean = true
    var cardOpacity: Int = 95
    var cardHeight: Int = 180
    var startTime: Int = 800
        set(varue) {
            startTimeInDay = TimeInDay(varue / 100, varue % 100)
            field = varue
        }
    var todayBGColor: Int = Color.parseColor("#10000000")
    var titleGravity: Int = Gravity.CENTER
    var titleAlpha: Int = 100
    var subtitleAlpha: Int = 60
    var drawNowLine: Boolean = true


    var startTimeInDay: TimeInDay? = null
    fun getStartTimeObject(): TimeInDay {
        startTimeInDay?.let {
            return it
        } ?: kotlin.run {
            startTimeInDay = TimeInDay(startTime / 100, startTime % 100)
            return startTimeInDay!!
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TimetableStyleSheet

        if (isColorEnabled != other.isColorEnabled) return false
        if (isFadeEnabled != other.isFadeEnabled) return false
        if (cardTitleColor != other.cardTitleColor) return false
        if (subTitleColor != other.subTitleColor) return false
        if (iconColor != other.iconColor) return false
        if (isBoldText != other.isBoldText) return false
        if (drawBGLine != other.drawBGLine) return false
        if (cardIconEnabled != other.cardIconEnabled) return false
        if (cardOpacity != other.cardOpacity) return false
        if (cardHeight != other.cardHeight) return false
        if (startTime != other.startTime) return false
        if (todayBGColor != other.todayBGColor) return false
        if (titleGravity != other.titleGravity) return false
        if (titleAlpha != other.titleAlpha) return false
        if (subtitleAlpha != other.subtitleAlpha) return false
        if (drawNowLine != other.drawNowLine) return false

        return true
    }

    override fun hashCode(): Int {
        var result = isColorEnabled.hashCode()
        result = 31 * result + isFadeEnabled.hashCode()
        result = 31 * result + cardTitleColor.hashCode()
        result = 31 * result + subTitleColor.hashCode()
        result = 31 * result + iconColor.hashCode()
        result = 31 * result + isBoldText.hashCode()
        result = 31 * result + drawBGLine.hashCode()
        result = 31 * result + cardIconEnabled.hashCode()
        result = 31 * result + cardOpacity
        result = 31 * result + cardHeight
        result = 31 * result + startTime
        result = 31 * result + todayBGColor
        result = 31 * result + titleGravity
        result = 31 * result + titleAlpha
        result = 31 * result + subtitleAlpha
        result = 31 * result + drawNowLine.hashCode()
        return result
    }


}
