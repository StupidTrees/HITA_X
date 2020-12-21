package com.stupidtree.hita.ui.base

/**
 * Trigger和LiveData配合使用，用于触发某种UI上的功能
 */
open class Trigger {
    enum class STATES {
        ACTION, STILL
    }

    var state = STATES.STILL
    fun setActioning() {
        state = STATES.ACTION
    }

    fun cancelActioning() {
        state = STATES.STILL
    }

    val isActioning: Boolean
        get() = state == STATES.ACTION

    companion object {
        val actioning: Trigger
            get() {
                val trigger = Trigger()
                trigger.setActioning()
                return trigger
            }

        val still: Trigger
            get() {
                val trigger = Trigger()
                trigger.cancelActioning()
                return trigger
            }
    }
}