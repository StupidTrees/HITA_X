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
    var isCurrent:Boolean = false

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TermItem

        if (yearCode != other.yearCode) return false
        if (termCode != other.termCode) return false

        return true
    }

    override fun hashCode(): Int {
        var result = yearCode.hashCode()
        result = 31 * result + termCode.hashCode()
        return result
    }

    fun getCode():String{
        return yearCode+termCode
    }

}