package com.stupidtree.hitax.ui.widgets.pullextend

/**
 * 下拉刷新和上拉加载更多的界面接口
 *
 * @author Li Hong
 * @since 2013-7-29
 */
interface IExtendLayout {
    /**
     * 得到当前的状态
     *
     * @return 状态
     */
    fun getState(): State

    /**
     * 设置当前状态，派生类应该根据这个状态的变化来改变View的变化
     *
     * @param state 状态
     */
    fun setState(state: State)

    /**
     * 得到当前Layout的内容大小，它将作为一个刷新的临界点
     *
     * @return 高度
     */
    fun getContentSize(): Int

    /**
     * 在拉动时调用
     *
     * @param offset 拉动的距离
     */
    fun onPull(offset: Int)

    /**
     * 当前的状态
     */
    enum class State {
        /**
         * Initial state
         */
        NONE,

        /**
         * When the UI is in a state which means that user is not interacting
         * with the Pull-to-Refresh function.
         */
        RESET,

        /**
         * 超出列表高度
         */
        beyondListHeight,

        /**
         * When the UI is currently refreshing, caused by a pull gesture.
         */
        startShowList,

        /**
         * 到达列表高度
         */
        arrivedListHeight
    }
}