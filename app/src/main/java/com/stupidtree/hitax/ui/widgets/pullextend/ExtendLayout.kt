package com.stupidtree.hitax.ui.widgets.pullextend

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.stupidtree.hitax.ui.widgets.pullextend.IExtendLayout
import java.lang.NullPointerException

/**
 * 这个类定义了Header和Footer的共通行为
 *
 * @author Li Hong
 * @since 2013-8-16
 */
abstract class ExtendLayout @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(
    context!!, attrs, defStyle
), IExtendLayout {
    /**
     * 当前的状态
     */
    private var mCurState = IExtendLayout.State.NONE
    /**
     * 得到前一个状态
     *
     * @return 状态
     */
    /**
     * 前一个状态
     */
    private var preState = IExtendLayout.State.NONE

    protected fun init(context: Context?, attrs: AttributeSet?) {
        val container = createLoadingView(context, attrs)
            ?: throw NullPointerException("Loading view can not be null.")
        val params = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )
        addView(container, params)
        bindView(container)
    }

    protected open fun bindView(container: View?) {}
    override fun getState(): IExtendLayout.State {
        return mCurState
    }

    override fun setState(state: IExtendLayout.State) {
        if (mCurState != state) {
            preState = mCurState
            mCurState = state
            onStateChanged(state, preState)
        }
    }

    override fun onPull(offset: Int) {}

    /**
     * 当状态改变时调用
     *
     * @param curState 当前状态
     * @param oldState 老的状态
     */
    private fun onStateChanged(curState: IExtendLayout.State?, oldState: IExtendLayout.State?) {
        when (curState) {
            IExtendLayout.State.RESET -> onReset()
            IExtendLayout.State.beyondListHeight -> onReleaseToRefresh()
            IExtendLayout.State.startShowList -> onRefreshing()
            IExtendLayout.State.arrivedListHeight -> onArrivedListHeight()
            else -> {}
        }
    }

    /**
     * 当状态设置为[State.RESET]时调用
     */
    protected open fun onReset() {}

    /**
     *
     */
    protected open fun onPullToRefresh() {}

    /**
     * 当状态设置为[State.beyondListHeight]时调用
     */
    protected open fun onReleaseToRefresh() {}

    /**
     * 当状态设置为[State.startShowList]时调用
     */
    protected open fun onRefreshing() {}

    /**
     * 当状态设置为[State.arrivedListHeight]时调用
     */
    protected open fun onArrivedListHeight() {}

    /**
     * 得到当前Layout的内容大小，它将作为一个刷新的临界点
     *
     * @return 高度
     */
    abstract override fun getContentSize(): Int
    abstract fun getListSize():Int

    /**
     * 创建Loading的View
     *
     * @param context context
     * @param attrs   attrs
     * @return Loading的View
     */
    protected abstract fun createLoadingView(context: Context?, attrs: AttributeSet?): View?

    init {
        init(context, attrs)
    }
}