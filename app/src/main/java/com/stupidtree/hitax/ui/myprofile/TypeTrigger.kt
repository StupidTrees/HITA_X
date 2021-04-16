package com.stupidtree.hitax.ui.myprofile

import com.stupidtree.component.data.Trigger


class TypeTrigger : Trigger() {
    var type = 0
    var subType: String? = null
    var typePermission: String? = null

    companion object {
        fun getActioning(type: Int, subType: String?, typePermission: String?): TypeTrigger {
            val typeTrigger = TypeTrigger()
            typeTrigger.setActioning()
            typeTrigger.type = type
            typeTrigger.subType = subType
            typeTrigger.typePermission = typePermission
            return typeTrigger
        }
    }
}