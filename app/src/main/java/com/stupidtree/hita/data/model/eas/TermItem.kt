package com.stupidtree.hita.data.model.eas

/**
 * 学年学期
 */
class TermItem(
     var yearCode: String,
     var yearName: String,
     var termCode: String,
     var termName: String
) {
    var name: String = yearName+termName


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TermItem

        if (yearCode != other.yearCode) return false
        if (termCode != other.termCode) return false

        return true
    }

    override fun hashCode(): Int {
        var result = yearCode?.hashCode() ?: 0
        result = 31 * result + (termCode?.hashCode() ?: 0)
        return result
    }


}