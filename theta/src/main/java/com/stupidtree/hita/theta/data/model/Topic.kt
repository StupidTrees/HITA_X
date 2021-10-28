package com.stupidtree.hita.theta.data.model

import java.sql.Timestamp

class Topic {
    var id: String = ""
    var name: String = ""
    var description: String = ""
    var avatar: String = ""
    var articleNum: Int = 0
    var createTime: Timestamp = Timestamp(0)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Topic

        if (id != other.id) return false
        if (name != other.name) return false
        if (description != other.description) return false
        if (avatar != other.avatar) return false
        if (articleNum != other.articleNum) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + avatar.hashCode()
        result = 31 * result + articleNum
        return result
    }


}