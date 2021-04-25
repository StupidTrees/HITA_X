package com.stupidtree.component.data

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