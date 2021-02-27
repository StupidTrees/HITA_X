package com.stupidtree.hitax.ui.eas.classroom

class BuildingItem {
    var name:String? = null
    var id:String = ""
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BuildingItem

        if (name != other.name) return false
        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name?.hashCode() ?: 0
        result = 31 * result + id.hashCode()
        return result
    }

}