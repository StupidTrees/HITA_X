package com.stupidtree.hitax.ui.main.timetable.views

import android.content.Context
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.text.TextUtils
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.stupidtree.hitax.R
import com.stupidtree.hitax.data.model.timetable.EventItem
import com.stupidtree.hitax.data.model.timetable.TimeInDay

class TimeTableBlockView
constructor(context: Context, var block: Any, var root: TimeTablePreferenceRoot) :
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

    private fun initEventCard(context: Context) {

        val ei = block as EventItem
        inflate(context, R.layout.fragment_timetable_class_card, this)
        card = findViewById(R.id.card)
        title = findViewById(R.id.title)
        subtitle = findViewById(R.id.subtitle)
        icon = findViewById(R.id.icon)
        var subjectColor = 0
        if (root.isColorEnabled) {
            val getC: Int = ei.color
            card.backgroundTintList = ColorStateList.valueOf(getC)
            subjectColor = getC
        } else {
            if (root.cardBackground == "primary") {
                card.backgroundTintList = ColorStateList.valueOf(root.colorPrimary)
            } else if (root.cardBackground == "accent") {
                card.backgroundTintList = ColorStateList.valueOf(root.colorAccent)
            }
        }
        when (root.cardTitleColor) {
            "subject" -> if (root.isColorEnabled) {
                title?.setTextColor(subjectColor)
            } else title?.setTextColor(root.colorPrimary)
            "white" -> title?.setTextColor(Color.WHITE)
            "black" -> title?.setTextColor(Color.BLACK)
            "primary" -> title?.setTextColor(root.colorPrimary)
            "accent" -> title?.setTextColor(root.colorAccent)
        }
        when (root.subTitleColor) {
            "subject" -> if (root.isColorEnabled) {
                subtitle?.setTextColor(subjectColor)
            } else subtitle?.setTextColor(root.colorPrimary)
            "white" -> subtitle?.setTextColor(Color.WHITE)
            "black" -> subtitle?.setTextColor(Color.BLACK)
            "primary" -> subtitle?.setTextColor(root.colorPrimary)
            "accent" -> subtitle?.setTextColor(root.colorAccent)
        }
        if (root.cardIconEnabled()) {
            icon?.visibility = VISIBLE
            icon?.setColorFilter(Color.WHITE)
            when (root.iconColor) {
                "subject" -> if (root.isColorEnabled) {
                    icon?.setColorFilter(subjectColor)
                } else icon?.setColorFilter(root.colorPrimary)
                "white" -> icon?.setColorFilter(Color.WHITE)
                "black" -> icon?.setColorFilter(Color.BLACK)
                "primary" -> icon?.setColorFilter(root.colorPrimary)
                "accent" -> icon?.setColorFilter(root.colorAccent)
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
        card.background.mutate().alpha = (255 * (root.cardOpacity.toFloat() / 100)).toInt()
        if (root.willBoldText()) {
            title?.typeface = Typeface.DEFAULT_BOLD
            subtitle?.typeface = Typeface.DEFAULT_BOLD
        }
        title?.alpha = root.titleAlpha.toFloat() / 100
        subtitle?.alpha = root.subtitleAlpha.toFloat() / 100
        title?.gravity = root.titleGravity
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
        val subjectColor = -1
        if (root.isColorEnabled) {
            val query: String =
                if (mainItem.type === EventItem.TYPE.EXAM && mainItem.name.endsWith("考试")) mainItem.name.substring(
                    0,
                    mainItem.name.length - 2
                ) else mainItem.name
            card.backgroundTintList = ColorStateList.valueOf(mainItem.color)
            when (root.cardTitleColor) {
                "subject" -> title?.setTextColor(subjectColor)
                "white" -> title?.setTextColor(Color.WHITE)
                "black" -> title?.setTextColor(Color.BLACK)
                "primary" -> title?.setTextColor(root.colorPrimary)
                "accent" -> title?.setTextColor(root.colorAccent)
            }
        } else {
            if (root.cardBackground == "primary") {
                card.backgroundTintList = ColorStateList.valueOf(root.colorPrimary)
            } else if (root.cardBackground == "accent") {
                card.backgroundTintList = ColorStateList.valueOf(root.colorAccent)
            }
            when (root.cardTitleColor) {
                "white" -> title?.setTextColor(Color.WHITE)
                "black" -> title?.setTextColor(Color.BLACK)
                "primary" -> title?.setTextColor(root.colorPrimary)
                "accent" -> title?.setTextColor(root.colorAccent)
            }
        }
        if (icon != null) {
            if (root.cardIconEnabled()) {
                icon?.visibility = VISIBLE
                icon?.setColorFilter(Color.WHITE)
                when (root.iconColor) {
                    "subject" -> if (root.isColorEnabled) {
                        icon?.setColorFilter(subjectColor)
                    } else icon?.setColorFilter(root.colorPrimary)
                    "white" -> icon?.setColorFilter(Color.WHITE)
                    "black" -> icon?.setColorFilter(Color.BLACK)
                    "primary" -> icon?.setColorFilter(root.colorPrimary)
                    "accent" -> icon?.setColorFilter(root.colorAccent)
                }
            } else {
                icon?.visibility = GONE
            }
        }
        card.background.mutate().alpha = (255 * (root.cardOpacity.toFloat() / 100)).toInt()
        if (root.willBoldText()) {
            title?.typeface = Typeface.DEFAULT_BOLD
        }
        title?.alpha = root.titleAlpha.toFloat() / 100
        title?.gravity = root.titleGravity
    }

    interface OnDuplicateCardLongClickListener {
        fun onDuplicateLongClick(v: View, list: List<EventItem>): Boolean
    }

    interface TimeTablePreferenceRoot {
        //        int locateFragment(FragmentTimeTablePage page);
        val isColorEnabled: Boolean
        val cardTitleColor: String?
        val subTitleColor: String?
        val iconColor: String?
        fun willBoldText(): Boolean
        fun drawBGLine(): Boolean
        fun cardIconEnabled(): Boolean
        val cardOpacity: Int
        val cardHeight: Int
        val startTime: TimeInDay
        val todayBGColor: Int
        val titleGravity: Int
        val colorPrimary: Int
        val colorAccent: Int
        val titleAlpha: Int
        val subtitleAlpha: Int
        fun animEnabled(): Boolean
        val cardBackground: String
        val tTPreference: SharedPreferences
        fun drawNowLine(): Boolean
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