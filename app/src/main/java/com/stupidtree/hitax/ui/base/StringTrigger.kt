package com.stupidtree.hitax.ui.base

class StringTrigger : Trigger() {
    lateinit var data: String

    companion object {
        fun getActioning(data: String): StringTrigger {
            val stringTrigger = StringTrigger()
            stringTrigger.setActioning()
            stringTrigger.data = data
            return stringTrigger
        }
    }
}