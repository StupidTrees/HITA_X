package com.stupidtree.hita.ui.widgets

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.stupidtree.hita.R

class RoundedBarView: FrameLayout {
    lateinit var text: TextView
    lateinit var icon: ImageView
    lateinit var card: View
    var iconPadding = 0
    var iconId = 0
    var selectedName:String?=null
    var dialogTitle: String? = null
    var titleTextColor:Int = Color.WHITE
    var backgroundRes = R.drawable.element_rounded_button_bg_white
    var selectedKey:String? = null
    var onClickListener:OnClickListener?=null
    set

    constructor(context: Context) : super(context) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        typedBarView(attrs, 0)
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        typedBarView(attrs, defStyleAttr)
        initView(context)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        typedBarView(attrs, defStyleAttr)
        initView(context)
    }

    private fun typedBarView(attrs: AttributeSet?, defStyleAttr: Int) {
        val a = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.RoundedBarPicker,
            defStyleAttr,
            0
        )
        val n = a.indexCount
        for (i in 0..n) {
            when (val attr = a.getIndex(i)) {
                R.styleable.RoundedBarPicker_barIcon -> iconId = a.getResourceId(attr, R.drawable.ic_baseline_check_24);
                R.styleable.RoundedBarPicker_defaultText -> selectedName = a.getString(attr)
                R.styleable.RoundedBarPicker_backgroundElement->backgroundRes = a.getResourceId(attr,R.drawable.element_rounded_button_bg_white)
                R.styleable.RoundedBarPicker_dialogTitle -> dialogTitle = a.getString(attr)
                R.styleable.RoundedBarPicker_iconPadding -> iconPadding = a.getDimensionPixelSize(
                    attr,
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 8f, resources.displayMetrics
                    ).toInt()
                )
                R.styleable.RoundedBarPicker_foregroundColor->titleTextColor = a.getColor(attr,Color.WHITE)
            }
        }
    }

    private fun initView(context: Context) {
        inflate(context, R.layout.view_bar_picker, this)
        icon = findViewById(R.id.icon)
        card = findViewById(R.id.card)
        text = findViewById(R.id.text)
        card.setOnClickListener {
            selectedKey?.let { it1 -> onClickListener?.onClick(it1) }
        }
        notifyView()
    }


    fun notifyView() {
        text.text = selectedName
        card.setBackgroundResource(backgroundRes)
        icon.setImageResource(iconId)
        text.setTextColor(titleTextColor)
        icon.setColorFilter(titleTextColor)
        icon.setPadding(iconPadding, iconPadding, iconPadding, iconPadding)
    }


    fun setValue(name:String,key:String){
        this.selectedName = name
        this.selectedKey = key
        notifyView()
    }

    interface OnClickListener{
        fun onClick(key:String)
    }



}