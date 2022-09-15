package com.stupidtree.hitax.ui.widgets.pullextend

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import android.widget.LinearLayout
import androidx.core.view.NestedScrollingParent
import com.stupidtree.hitax.ui.widgets.pullextend.ExtendListHeader.OnExpandListener
import kotlin.math.abs
import kotlin.math.max

/**
 * 这个实现了下拉刷新和上拉加载更多的功能
 *
 * @author Li Hong
 * @since 2013-7-29
 */
open class PullExtendLayout @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : LinearLayout(context, attrs, defStyle), IPullToExtend, NestedScrollingParent {
    /**
     * 主View
     */
    var mRefreshableView: View? = null

    /**
     * 阻尼系数
     */
    var mOffsetRadio = 1f

    /**
     * 上一次移动的点Y轴
     */
    private var mLastMotionY = -1f
    private var mPullDown:Boolean? = false

    /**
     * 下拉刷新的布局
     */
    private var mHeaderLayout: ExtendListHeader? = null

    /**
     * 列表开始显示的高度
     */
    private var mHeaderHeight = 0

    /**
     * 列表的高度
     */
    private var mHeaderListHeight = 0

    /**
     * 平滑滚动的Runnable
     */
    private var mSmoothScrollRunnable: SmoothScrollRunnable? = null

    /**
     * 是否已经展开
     */
    private var isHeadExpanded = false

    /**
     * 底部是否已经被弹性拉起
     */
    private var onPulledListener: OnPulledListener? = null
    private var onExpandListener: OnExpandListener? = null
    fun setOnExpandListener(onExpandListener: OnExpandListener) {
        this.onExpandListener = onExpandListener
        mHeaderLayout?.setOnExpandListener(onExpandListener)
    }

    fun setOnPulledListener(onPulledListener: OnPulledListener?) {
        this.onPulledListener = onPulledListener
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        val childCount = childCount
        if (childCount == 2) {
            if (getChildAt(0) is ExtendLayout) {
                mHeaderLayout = getChildAt(0) as ExtendListHeader
                mRefreshableView = getChildAt(1)
            } else {
                mRefreshableView = getChildAt(0)
            }
        } else if (childCount == 3) {
            if (getChildAt(0) is ExtendLayout) {
                mHeaderLayout = getChildAt(0) as ExtendListHeader
            }
            mRefreshableView = getChildAt(1)
        } else {
            throw IllegalStateException("布局异常，最多三个，最少一个")
        }
        checkNotNull(mRefreshableView) { "布局异常，一定要有内容布局" }
        // mRefreshableView.setClickable(true);需要自己设置
        init(context)
    }

    fun setOffsetRadio(offsetRadio: Float) {
        mOffsetRadio = offsetRadio
    }

    /**
     * 重置header
     * 在下拉时抬起时，根据滚动距离来决定头部站看还是收缩
     * scrollY < mHeaderHeight 回弹下拉，使头部收缩（MHeaderHeight是下来刷新的零界点）
     * scrollY >= mHeaderHeight 下拉超出了头部布局，布局回弹到头部布局高度
     */
    var isAnimating = false

    /**
     * 初始化padding，我们根据header和footer的高度来设置top padding和bottom padding
     */
    private fun refreshLoadingViewsSize() {
        // 得到header和footer的内容高度，它将会作为拖动刷新的一个临界值，如果拖动距离大于这个高度
        // 然后再松开手，就会触发刷新操作
        var headerHeight =  mHeaderLayout?.getContentSize()?:0
        mHeaderListHeight = mHeaderLayout?.getContentSize()?:0
        if (headerHeight < 0) {
            headerHeight = 0
        }
        mHeaderHeight = headerHeight
        // 这里得到Header和Footer的高度，设置的padding的top和bottom就应该是header和footer的高度
        // 因为header和footer是完全看不见的
        headerHeight = mHeaderLayout?.measuredHeight ?:0
        val pLeft = paddingLeft
        val pTop = -headerHeight
        val pRight = paddingRight
        val pBottom = 0
        setPadding(pLeft, pTop, pRight, pBottom)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        refreshLoadingViewsSize()
        // 设置刷新View的大小
        refreshRefreshableViewSize(w, h)
        post { requestLayout() }
    }

    /**
     * 计算刷新View的大小
     *
     * @param width  当前容器的宽度
     * @param height 当前容器的宽度
     */
    protected fun refreshRefreshableViewSize(width: Int, height: Int) {
        val lp = mRefreshableView!!.layoutParams as LayoutParams
        if (lp.height != height) {
            lp.height = height
            mRefreshableView!!.requestLayout()
        }
    }

    /**
     * 初始化
     *
     * @param context context
     */
    private fun init(context: Context) {
        //  mTouchSlop = (int) (ViewConfiguration.get(context).getScaledTouchSlop() * 1.5);
        val layoutParams = mRefreshableView!!.layoutParams
        layoutParams.height = 10
        mRefreshableView!!.layoutParams = layoutParams
    }

    fun callWhenChildScrolled(ev: MotionEvent) {
        if (ev.action == MotionEvent.ACTION_MOVE) {
            val deltaY = ev.y - mLastMotionY
            mLastMotionY = ev.y
            if (deltaY != 0f) {
                mPullDown = deltaY > 0
            }
        }
    }

    override fun onStartNestedScroll(child: View, target: View, nestedScrollAxes: Int): Boolean {
        return true
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) {
        super.onNestedPreScroll(target, dx, dy, consumed)
        if (scrollYValue < 0f && dy > 0f) {
            pullHeaderLayout(-dy.toFloat())
            consumed[1] = dy
        }
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int
    ) {
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed)
        if (dyUnconsumed < 0) {
            if (dyConsumed == 0) {
                mPullDown = true
                pullHeaderLayout(-dyUnconsumed / mOffsetRadio)
            }
        } else if (dyUnconsumed > 0) {
            mPullDown = false
            pullFooterLayout(-dyUnconsumed.toFloat())
        }
        Log.e("onNestedScroll","$dyUnconsumed ,$dyConsumed");
    }

    override fun getHeaderExtendLayout(): ExtendLayout {
        return mHeaderLayout!!
    }

    /**
     * 判断刷新的View是否滑动到顶部
     *
     * @return true表示已经滑动到顶部，否则false
     */
    protected fun isReadyForPullDown(deltaY: Float): Boolean {
        //if(mRefreshableView.canScrollVertically(-1)) return false;
        return scrollYValue < 0 || scrollYValue == 0 && deltaY > 0
    }

    /**
     * 判断刷新的View是否滑动到底
     *
     * @return true表示已经滑动到底部，否则false
     */
    protected fun isReadyForPullUp(deltaY: Float): Boolean {
        //if(mRefreshableView.canScrollVertically(1)) return false;
        return scrollYValue > 0 || scrollYValue == 0 && deltaY < 0
    }

    /**
     * 得到平滑滚动的时间，派生类可以重写这个方法来控件滚动时间
     *
     * @return 返回值时间为毫秒
     */
    private val smoothScrollDuration: Long
        get() = SCROLL_DURATION.toLong()

    override fun onStopNestedScroll(child: View) {
        super.onStopNestedScroll(child)
        if (isReadyForPullDown(0f)) {
            if (mPullDown==true) {
                // 弹性展开头部
                if (isHeadExpanded) {
                    collapseHeaderLayout()
                    mPullDown = null
                } else{
                    resetHeaderLayout()
                    mPullDown = null
                }
            } else {
                // 往上拉时，收缩头部，不弹性会弹
                collapseHeaderLayout()
                mPullDown = null
            }
        } else if (isReadyForPullUp(0f)) {
           onExpandListener?.onCollapseStart()
            smoothScrollTo(0)
        }
    }

    private val decelerateFactor: Float
        get() = 5f

    /**
     * 拉动Header Layout时调用
     *
     * @param delta 移动的距离
     */
    private fun pullHeaderLayout(delta: Float) {
        if (isAnimating) return
        setScrollBy(0, -(delta * 0.6f).toInt())
        // 未处于刷新状态，更新箭头
        val scrollY = abs(scrollYValue)
        if (null != mHeaderLayout && 0 != mHeaderHeight) {
            if (scrollY >= mHeaderListHeight) {
                mHeaderLayout?.setState(IExtendLayout.State.arrivedListHeight)
                setOffsetRadio(4.0f)
            } else {
                setOffsetRadio(1.0f)
            }
            mHeaderLayout?.onPull(scrollY)
        }
    }

    /**
     * 拉Footer时调用
     *
     * @param delta 移动的距离
     */
    private fun pullFooterLayout(delta: Float) {
        if (isAnimating) return
        setScrollBy(0, -(delta * 0.2).toInt())
    }

    private fun resetHeaderLayout() {
        val scrollY = abs(scrollYValue)
        if (scrollY < mHeaderHeight/2) {
            Log.d("xxx", "resetHeaderLayout,scrollY < mHeaderHeight,收起来");
            smoothScrollTo(0)
            isHeadExpanded = false
        } else if(!isHeadExpanded){
            Log.d("xxx", "resetHeaderLayout,scrollY >= mHeaderHeight，展开");
            isHeadExpanded = true
            smoothScrollTo(-mHeaderListHeight)
        }
    }

    /**
     * 在上拉抬起时，直接收起头部布局，按QQ，抬起时，不需要回弹效果
     */
    private fun collapseHeaderLayout() {
        onExpandListener?.onCollapseStart()
        isHeadExpanded = false
        smoothScrollTo(0)
    }

    /**
     * 隐藏header和footer
     */
    fun closeExtendHeadAndFooter() {
        smoothScrollTo(0)
        isHeadExpanded = false
    }

    /**
     * 设置滚动位置
     *
     * @param x 滚动到的x位置
     * @param y 滚动到的y位置
     */
    private fun setScrollTo(x: Int, y: Int) {
        scrollTo(x, y)
    }

    /**
     * 设置滚动的偏移
     *
     * @param x 滚动x位置
     * @param y 滚动y位置
     */
    private fun setScrollBy(x: Int, y: Int) {
        scrollBy(x, y)
    }

    /**
     * 得到当前Y的滚动值
     *
     * @return 滚动值
     * getScrollY < 0 ，说明上边已滑出屏幕
     */
    private val scrollYValue: Int
        private get() = scrollY

    /**
     * 平滑滚动
     *
     * @param newScrollValue 滚动的值
     */
    private fun smoothScrollTo(newScrollValue: Int) {
        isAnimating = true
        //  Log.e("animating","start");
        //Log.d(TAG, "smoothScrollTo,newScrollValue = " + newScrollValue);
        smoothScrollTo(newScrollValue, smoothScrollDuration, decelerateFactor)
    }

    /**
     * 平滑滚动
     *
     * @param newScrollValue 滚动的值
     * @param duration       滚动时候
     */
    private fun smoothScrollTo(newScrollValue: Int, duration: Long, decFactor: Float) {
        var newScrollValue = newScrollValue
        if (null != mSmoothScrollRunnable) {
            mSmoothScrollRunnable!!.stop()
        }
        val oldScrollValue = scrollYValue
        val post = oldScrollValue != newScrollValue
        if (post) {
            mSmoothScrollRunnable =
                SmoothScrollRunnable(oldScrollValue, newScrollValue, duration, decFactor)
        } else {
            // 修正
            if (oldScrollValue < 0) {
                newScrollValue = 0
            }
            mSmoothScrollRunnable =
                SmoothScrollRunnable(oldScrollValue, newScrollValue, duration, decFactor)
        }

//        if (post) {
        post(mSmoothScrollRunnable)
        //        }
    }

    interface OnPulledListener {
        fun onReleaseAfterPulledUp()
    }

    /**
     * 实现了平滑滚动的Runnable
     *
     * @author Li Hong
     * @since 2013-8-22
     */
    internal inner class SmoothScrollRunnable(
        /**
         * 开始Y
         */
        private val mScrollFromY: Int,
        /**
         * 结束Y
         */
        private val mScrollToY: Int,
        /**
         * 滑动时间
         */
        private val mDuration: Long, decFactor: Float
    ) : Runnable {
        /**
         * 动画效果
         */
        private val mInterpolator: Interpolator

        /**
         * 是否继续运行
         */
        private var mContinueRunning = true

        /**
         * 开始时刻
         */
        private var mStartTime: Long = -1

        /**
         * 当前Y
         */
        private var mCurrentY = -1
        fun setAnimatingFalse() {
            isAnimating = false
            //     Log.e("isAnimating","false");
        }

        override fun run() {
            if (mDuration <= 0) {
                setScrollTo(0, mScrollToY)
                return
            }
            if (mStartTime == -1L) {
                mStartTime = System.currentTimeMillis()
            } else {
                val oneSecond: Long = 1000 // SUPPRESS CHECKSTYLE
                var normalizedTime =
                    oneSecond * (System.currentTimeMillis() - mStartTime) / mDuration
                normalizedTime = Math.max(Math.min(normalizedTime, oneSecond), 0)
                val deltaY = Math.round(
                    (mScrollFromY - mScrollToY)
                            * mInterpolator.getInterpolation(normalizedTime / oneSecond.toFloat())
                )
                mCurrentY = mScrollFromY - deltaY
                setScrollTo(0, mCurrentY)
                if (abs(mCurrentY) >= mHeaderListHeight - 1 || Math.abs(mCurrentY) <= 3) setAnimatingFalse()
                if (null != mHeaderLayout && 0 != mHeaderHeight) {
                    //Log.d(TAG, "run  mHeaderLayout.onPull =" + Math.abs(mCurrentY));
                    mHeaderLayout!!.onPull(Math.abs(mCurrentY))
                    if (mCurrentY == 0) {
                        mHeaderLayout!!.setState(IExtendLayout.State.RESET)
                    }
                    if (abs(mCurrentY) == mHeaderListHeight) {
                        mHeaderLayout!!.setState(IExtendLayout.State.arrivedListHeight)
                    }
                }
            }

            // If we're not at the target Y, keep going...
            if (mContinueRunning && mScrollToY != mCurrentY) {
                postDelayed(this, 16) // SUPPRESS CHECKSTYLE
            }
        }

        /**
         * 停止滑动
         */
        fun stop() {
            mContinueRunning = false
            removeCallbacks(this)
        }

        /**
         * 构造方法
         *
         * @param fromY    开始Y
         * @param toY      结束Y
         * @param duration 动画时间
         */
        init {
            mInterpolator = DecelerateInterpolator(decFactor)
        }
    }

    companion object {
        /**
         * 回滚的时间
         */
        private const val SCROLL_DURATION = 800
    }

    init {
        orientation = VERTICAL
    }
}