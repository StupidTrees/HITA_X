package com.stupidtree.hitax.ui.widgets

import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.stupidtree.hitax.R

class SelectableIconCardView : FrameLayout {
    var icon: ImageView? = null
    var iconBG: ImageView? = null
    var card: CardView? = null
    var title: TextView? = null
    var subtitle: TextView? = null
    var checkable = false
    var iconId = 0
    var iconColor = 0
    var backgroundTint = -1
    var titleStr: String? = null
    var subtitleStr: String? = null
    var isChecked = false
    var hapticFeedback = false
    var iconPadding = 0
    var onCheckChangeListener: OnCheckChangedListener? = null
    var onCardClickListener: OnClickListener? = null

    constructor(context: Context) : super(context) {
        initCard(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        typedSICardView(attrs, 0)
        initCard(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        typedSICardView(attrs, defStyleAttr)
        initCard(context)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        typedSICardView(attrs, defStyleAttr)
    }


    private fun typedSICardView(attrs: AttributeSet?, defStyleAttr: Int) {
        val a = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.SelectableIconCardView,
            defStyleAttr,
            0
        )
        val n = a.indexCount
        for (i in 0..n) {
            when (val attr = a.getIndex(i)) {
                R.styleable.SelectableIconCardView_cardCheckable -> checkable =
                    a.getBoolean(attr, true)
                R.styleable.SelectableIconCardView_cardIcon -> iconId =
                    a.getResourceId(attr, R.drawable.ic_baseline_access_alarm_24)
                R.styleable.SelectableIconCardView_cardIconColor -> iconColor =
                    a.getColor(attr, R.attr.colorPrimary)
                R.styleable.SelectableIconCardView_cardBGTint -> backgroundTint =
                    a.getColor(attr, -1)

                R.styleable.SelectableIconCardView_cardTitleText -> titleStr = a.getString(attr)
                R.styleable.SelectableIconCardView_cardSubtitleText -> subtitleStr =
                    a.getString(attr)
                R.styleable.SelectableIconCardView_cardHapticFeedback -> hapticFeedback =
                    a.getBoolean(attr, true)
                R.styleable.SelectableIconCardView_cardIconPadding -> iconPadding =
                    a.getDimensionPixelSize(
                        attr, TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, 8f, resources.displayMetrics
                        ).toInt()
                    )
            }
        }
    }

    private fun initCard(context: Context?) {
        inflate(context, R.layout.dynamic_selectable_icon_card, this)
        icon = findViewById(R.id.icon)
        iconBG = findViewById(R.id.iconBG)
        card = findViewById(R.id.card)
        title = findViewById(R.id.title)
        subtitle = findViewById(R.id.subtitle)
        icon?.setImageResource(iconId)
        title?.text = titleStr
        subtitle?.text = subtitleStr
        card?.setOnClickListener { v ->
            if (hapticFeedback) v.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
            if (onCardClickListener != null) onCardClickListener!!.onClick(v)
            toggle()
        }
        icon?.setPadding(iconPadding, iconPadding, iconPadding, iconPadding)

        refreshState()
    }

    fun setTitle(text: Int) {
        title!!.setText(text)
    }

    fun setTitle(text: String?) {
        if (TextUtils.isEmpty(text)) title!!.setText(R.string.none) else title!!.text = text
    }

    fun setSubtitle(text: Int) {
        subtitle!!.setText(text)
    }

    fun check(checked: Boolean) {
        if (!checkable) return
        isChecked = checked
        refreshState()
    }

    fun toggle() {
        if (!checkable) return
        isChecked = !isChecked
        refreshState()
        if (onCheckChangeListener != null) onCheckChangeListener!!.onCheckChanged(card, isChecked)
    }

    fun getTitleString(): String {
        return title!!.text.toString()
    }

    private fun refreshState() {
        if (isEnabled) {
            icon?.setBackgroundResource(R.drawable.element_round_white_light)
            if (isChecked || !checkable) {
                icon!!.setColorFilter(iconColor)
                if (backgroundTint != -1) {
                    iconBG?.setColorFilter(backgroundTint)
                }else{
                    iconBG?.setColorFilter(iconColor)
                }
            } else {
                icon!!.clearColorFilter()
                iconBG?.setColorFilter(Color.GRAY)
            }
        } else {
            icon!!.clearColorFilter()
            icon!!.setBackgroundResource(R.drawable.element_round_grey)
            icon!!.setColorFilter(Color.GRAY)
        }
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        refreshState()
    }

    interface OnCheckChangedListener {
        fun onCheckChanged(v: View?, newChecked: Boolean)
    }
}