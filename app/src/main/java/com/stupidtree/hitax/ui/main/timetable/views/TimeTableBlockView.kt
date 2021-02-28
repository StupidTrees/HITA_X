package com.stupidtree.hitax.ui.main.timetable.views

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.text.TextUtils
import android.util.TypedValue
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.stupidtree.hitax.R
import com.stupidtree.hitax.data.model.timetable.EventItem
import com.stupidtree.hitax.ui.main.timetable.inner.TimetableStyleSheet

class TimeTableBlockView constructor(context: Context, var block: Any, var styleSheet: TimetableStyleSheet) :
        FrameLayout(context) {
    lateinit var card: View
    var title: TextView? = null
    var subtitle: TextView? = null
    var icon: ImageView? = null
    var onCardClickListener: OnCardClickListener? = null
    var onCardLongClickListener: OnCardLongClickListener? = null
    var onDuplicateCardClickListener: OnDuplicateCardClickListener? = null
    var onDuplicateCardLongClickListener: OnDuplicateCardLongClickListener? = null

    interface OnCardClickListener {
        fun onClick(v: View, ei: EventItem)
    }

    interface OnCardLongClickListener {
        fun onLongClick(v: View, ei: EventItem): Boolean
    }

    interface OnDuplicateCardClickListener {
        fun onDuplicateClick(v: View, list: List<EventItem>)
    }

    private fun getColor(color: Int): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(color, typedValue, true)
        return typedValue.data
    }

    private fun initEventCard(context: Context) {
        val ei = block as EventItem
        inflate(context, R.layout.fragment_timetable_class_card, this)
        card = findViewById(R.id.card)
        title = findViewById(R.id.title)
        subtitle = findViewById(R.id.subtitle)
        icon = findViewById(R.id.icon)
        if(styleSheet.isFadeEnabled){
            card.setBackgroundResource(R.drawable.spec_timetable_card_background_fade)
        }else{
            card.setBackgroundResource(R.drawable.spec_timetable_card_background)
        }
        if (styleSheet.isColorEnabled) {
            card.backgroundTintList = ColorStateList.valueOf(ei.color)
        } else {
            card.backgroundTintList = ColorStateList.valueOf(getColor(R.attr.colorPrimary))
        }
        when (styleSheet.cardTitleColor) {
            "subject" -> if (styleSheet.isColorEnabled) {
                title?.setTextColor(ei.color)
            } else title?.setTextColor(getColor(R.attr.colorPrimary))
            "white" -> title?.setTextColor(Color.WHITE)
            "black" -> title?.setTextColor(Color.BLACK)
            "primary" -> title?.setTextColor(getColor(R.attr.colorPrimary))
        }
        when (styleSheet.subTitleColor) {
            "subject" -> if (styleSheet.isColorEnabled) {
                subtitle?.setTextColor(ei.color)
            } else subtitle?.setTextColor(getColor(R.attr.colorPrimary))
            "white" -> subtitle?.setTextColor(Color.WHITE)
            "black" -> subtitle?.setTextColor(Color.BLACK)
            "primary" -> subtitle?.setTextColor(getColor(R.attr.colorPrimary))
        }
        if (styleSheet.cardIconEnabled) {
            icon?.visibility = VISIBLE
            icon?.setColorFilter(Color.WHITE)
            when (styleSheet.iconColor) {
                "subject" -> if (styleSheet.isColorEnabled) {
                    icon?.setColorFilter(ei.color)
                } else icon?.setColorFilter(getColor(R.attr.colorPrimary))
                "white" -> icon?.setColorFilter(Color.WHITE)
                "black" -> icon?.setColorFilter(Color.BLACK)
                "primary" -> icon?.setColorFilter(getColor(R.attr.colorPrimary))
            }
        } else {
            icon?.visibility = GONE
        }

        card.setOnClickListener { v -> onCardClickListener?.onClick(v, ei) }
        card.setOnLongClickListener { v: View ->
            return@setOnLongClickListener onCardLongClickListener?.onLongClick(v, ei) == true
        }
        title?.text = ei.name
        subtitle?.text = if (TextUtils.isEmpty(ei.place)) "" else ei.place
        card.background.mutate().alpha = (255 * (styleSheet.cardOpacity.toFloat() / 100)).toInt()
        if (styleSheet.isBoldText) {
            title?.typeface = Typeface.DEFAULT_BOLD
            subtitle?.typeface = Typeface.DEFAULT_BOLD
        }
        title?.alpha = styleSheet.titleAlpha.toFloat() / 100
        subtitle?.alpha = styleSheet.subtitleAlpha.toFloat() / 100
        title?.gravity = styleSheet.titleGravity
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (block is EventItem) {
            initEventCard(context)
        } else if (block is List<*>) {
            initDuplicateCard(context)
        }
    }


    private fun initDuplicateCard(context: Context) {
        val list: List<EventItem> = block as List<EventItem>
        inflate(context, R.layout.fragment_timetable_duplicate_card, this)
        card = findViewById(R.id.card)
        title = findViewById(R.id.title)
        icon = findViewById(R.id.icon)
        val sb = StringBuilder()
        for (ei in list) sb.append(ei.name).append(";\n")
        title?.text = sb.toString()
        if (onDuplicateCardClickListener != null) card.setOnClickListener { v ->
            onDuplicateCardClickListener?.onDuplicateClick(
                    v,
                    list
            )
        }
        if (onDuplicateCardLongClickListener != null) card.setOnLongClickListener { v ->
            onDuplicateCardLongClickListener?.let { return@let it.onDuplicateLongClick(v, list) }
            return@setOnLongClickListener false
        }
        val mainItem = list[0]
        if (styleSheet.isColorEnabled) {
            card.backgroundTintList = ColorStateList.valueOf(mainItem.color)
            when (styleSheet.cardTitleColor) {
                "subject" -> title?.setTextColor(mainItem.color)
                "white" -> title?.setTextColor(Color.WHITE)
                "black" -> title?.setTextColor(Color.BLACK)
                "primary" -> title?.setTextColor(getColor(R.attr.colorPrimary))
            }
        } else {
            card.backgroundTintList = ColorStateList.valueOf(getColor(R.attr.colorPrimary))
            when (styleSheet.cardTitleColor) {
                "white" -> title?.setTextColor(Color.WHITE)
                "black" -> title?.setTextColor(Color.BLACK)
                "primary" -> title?.setTextColor(getColor(R.attr.colorPrimary))
            }
        }
        if (icon != null) {
            if (styleSheet.isColorEnabled) {
                icon?.visibility = VISIBLE
                icon?.setColorFilter(Color.WHITE)
                when (styleSheet.iconColor) {
                    "subject" -> if (styleSheet.isColorEnabled) {
                        icon?.setColorFilter(mainItem.color)
                    } else icon?.setColorFilter(getColor(R.attr.colorPrimary))
                    "white" -> icon?.setColorFilter(Color.WHITE)
                    "black" -> icon?.setColorFilter(Color.BLACK)
                    "primary" -> icon?.setColorFilter(getColor(R.attr.colorPrimary))
                }
            } else {
                icon?.visibility = GONE
            }
        }
        card.background.mutate().alpha = (255 * (styleSheet.cardOpacity.toFloat() / 100)).toInt()
        if (styleSheet.isBoldText) {
            title?.typeface = Typeface.DEFAULT_BOLD
        }
        title?.alpha = styleSheet.titleAlpha.toFloat() / 100
        title?.gravity = styleSheet.titleGravity
    }

    interface OnDuplicateCardLongClickListener {
        fun onDuplicateLongClick(v: View, list: List<EventItem>): Boolean
    }


    fun getDow(): Int {
        if (block is EventItem) {
            return (block as EventItem).getDow()
        } else if (block is List<*>) {
            return (block as List<EventItem>)[0].getDow()
        }
        return -1
    }

    fun getDuration(): Int {
        if (block is EventItem) {
            return (block as EventItem).getDurationInMinutes()
        } else if (block is List<*>) {
            return ((block as List<*>)[0] as EventItem).getDurationInMinutes()
        }
        return -1
    }

    fun getEvent(): EventItem {
        if (block is List<*>) {
            return (block as List<*>)[0] as EventItem
        }
        return block as EventItem
    }


}