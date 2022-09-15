package com.stupidtree.hitax.ui.widgets.pullextend

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.stupidtree.hitax.R
import com.stupidtree.hitax.utils.ImageUtils.dp2px
import kotlin.math.abs

/**
 * 这个类封装了下拉刷新的布局
 */
class ExtendListHeader : ExtendLayout {
    var progress = 0f
    var containerHeight = dp2px(context, 200f).toFloat()
    var listHeight = dp2px(context, 320f).toFloat()
    var arrivedListHeight = false

    //return mCurState==State.arrivedListHeight||mCurState==State.beyondListHeight;
    var isExpanded = false
    private var onExpandListener: OnExpandListener? = null
    var headerViewGroup: ViewGroup? = null
        private set

    /**
     * 原点
     */
    private var mExpendPoint: ExpendPoint? = null

    /**
     * 构造方法
     *
     * @param context context
     */
    constructor(context: Context?) : super(context)

    /**
     * 构造方法
     *
     * @param context context
     * @param attrs   attrs
     */
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    fun setOnExpandListener(onExpandListener: OnExpandListener?) {
        this.onExpandListener = onExpandListener
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // 在此处获取listHeight 和 containerHeight值
        listHeight = if (headerViewGroup == null) dp2px(
            context, 200f
        )
            .toFloat() else headerViewGroup!!.measuredHeight.toFloat()
        containerHeight = listHeight / 2
    }

    override fun bindView(container: View?) {
        headerViewGroup = findViewById(R.id.header)
        mExpendPoint = findViewById(R.id.expend_point)
    }

    @SuppressLint("InflateParams")
    override fun createLoadingView(context: Context?, attrs: AttributeSet?): View? {
        return LayoutInflater.from(context).inflate(R.layout.extend_header, null)
    }

    override fun getContentSize(): Int {
        return containerHeight.toInt()
    }

    override fun getListSize(): Int{
        return listHeight.toInt()
    }

    override fun onReset() {
        mExpendPoint!!.visibility = VISIBLE
        mExpendPoint!!.alpha = 1f
        mExpendPoint!!.translationY = 0f
        headerViewGroup!!.translationY = 0f
        progress = 0f
        arrivedListHeight = false
        isExpanded = false
        onExpandListener?.onCollapse()
    }

    override fun onReleaseToRefresh() {}
    override fun onPullToRefresh() {}
    override fun onArrivedListHeight() {
        arrivedListHeight = true
    }

    override fun onRefreshing() {}
    override fun onPull(offset: Int) {
        if (!arrivedListHeight) {
            mExpendPoint!!.visibility = VISIBLE
            val percent = abs(offset) / containerHeight
            val moreOffset = abs(offset) - containerHeight.toInt()
            progress = 1.0f.coerceAtMost(percent)
            if (percent <= 1f) {
                if (isExpanded) {
                    onExpandListener?.onCollapse()
                    isExpanded = false
                }
                mExpendPoint!!.setPercent(percent)
                mExpendPoint!!.translationY =
                    (-abs(offset) / 2.0 + mExpendPoint!!.height / 2.0).toFloat()
                headerViewGroup!!.translationY = -containerHeight
            } else {
                if (!isExpanded) {
                    onExpandListener?.onExpand()
                    performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                    isExpanded = true
                }
                var subPercent = moreOffset / (listHeight - containerHeight)
                subPercent = 1.0f.coerceAtMost(subPercent)
                mExpendPoint?.translationY =
                    (-containerHeight.toInt() shr 1) + (mExpendPoint!!.height shr 1) + containerHeight.toInt() * subPercent / 2
                mExpendPoint?.setPercent(1.0f)
                val alpha = 1 - subPercent * 2
                mExpendPoint?.alpha = alpha.coerceAtLeast(0f)
                headerViewGroup!!.translationY = -(1 - subPercent) * containerHeight
            }
        }
        if (abs(offset) >= listHeight) {
            mExpendPoint!!.visibility = INVISIBLE
            headerViewGroup!!.translationY = -(abs(offset) - listHeight) / 2
        }
    }

    interface OnExpandListener {
        fun onExpand()
        fun onCollapseStart()
        fun onCollapse()
    }
}