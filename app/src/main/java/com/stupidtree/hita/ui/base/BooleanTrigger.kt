package com.stupidtree.hita.ui.base

class BooleanTrigger : Trigger() {
    var data: Boolean = false

    companion object {
        fun getActioning(data: Boolean): BooleanTrigger {
            val stringTrigger = BooleanTrigger()
            stringTrigger.setActioning()
            stringTrigger.data = data
            return stringTrigger
        }
    }
}