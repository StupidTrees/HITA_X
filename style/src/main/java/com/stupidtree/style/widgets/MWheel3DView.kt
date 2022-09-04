package com.stupidtree.style.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.HapticFeedbackConstants
import com.cncoderx.wheelview.OnWheelChangedListener
import com.cncoderx.wheelview.Wheel3DView
import com.cncoderx.wheelview.WheelView

/*加上震动反馈*/
class MWheel3DView : Wheel3DView {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    override fun setOnWheelChangedListener(onWheelChangedListener: OnWheelChangedListener) {
        val lis = OnWheelChangedListener { view: WheelView, oldIndex: Int, newIndex: Int ->
            onWheelChangedListener.onChanged(view, oldIndex, newIndex)
            if (oldIndex != newIndex) view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
        }
        super.setOnWheelChangedListener(lis)
    }

}