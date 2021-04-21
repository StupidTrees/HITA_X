package com.stupidtree.hita.theta.data.model

import java.io.Serializable
import java.sql.Time
import java.sql.Timestamp

class Article :Serializable{
    var id: String = ""
    var authorId: String = ""
    var authorName: String = ""
    var authorAvatar: String = ""
    var repostId: String? = null
    var repostAuthorId: String? = null
    var repostAuthorName: String? = null
    var repostContent: String? = null
    var repostTime: Timestamp = Timestamp(0)
    var content: String = ""
    var commentNum: Int = 0
    var likeNum: Int = 0
    var liked: Boolean = false

    var createTime: Timestamp = Timestamp(0)
    var updateTime: Timestamp = Timestamp(0)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Article

        if (id != other.id) return false
        if (authorId != other.authorId) return false
        if (authorName != other.authorName) return false
        if (authorAvatar != other.authorAvatar) return false
        if (repostId != other.repostId) return false
        if (repostAuthorName != other.repostAuthorName) return false
        if (repostContent != other.repostContent) return false
        if (content != other.content) return false
        if (commentNum != other.commentNum) return false
        if (likeNum != other.likeNum) return false
        if (liked != other.liked) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + authorId.hashCode()
        result = 31 * result + authorName.hashCode()
        result = 31 * result + authorAvatar.hashCode()
        result = 31 * result + (repostId?.hashCode() ?: 0)
        result = 31 * result + (repostAuthorName?.hashCode() ?: 0)
        result = 31 * result + (repostContent?.hashCode() ?: 0)
        result = 31 * result + content.hashCode()
        result = 31 * result + commentNum
        result = 31 * result + likeNum
        result = 31 * result + liked.hashCode()
        return result
    }


}