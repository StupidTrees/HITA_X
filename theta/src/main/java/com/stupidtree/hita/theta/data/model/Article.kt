package com.stupidtree.hita.theta.data.model

import java.io.Serializable
import java.sql.Time
import java.sql.Timestamp

class Article : Serializable {

    enum class TYPE { NORMAL, VOTE }
    enum class VOTED { NONE, UP, DOWN }

    var id: String = ""
    var authorId: String = ""
    var authorName: String = ""
    var authorAvatar: String = ""
    var repostId: String? = null
    var repostAuthorId: String? = null

    var topicId: String? = null
    var topicName: String? = null
    var repostAuthorAvatar: String? = null
    var repostAuthorName: String? = null
    var repostContent: String? = null
    var repostTime: Timestamp = Timestamp(0)
    var content: String = ""
    var commentNum: Int = 0
    var likeNum: Int = 0
    var type: TYPE = TYPE.NORMAL
    var upNum: Int = 0
    var downNum: Int = 0
    var votedUp: VOTED = VOTED.NONE
    var isMine: Boolean = false
    var liked: Boolean = false
    var starred: Boolean = false
    var images: List<String> = listOf()
    var repostImages: List<String> = listOf()

    var createTime: Timestamp = Timestamp(0)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Article

        if (id != other.id) return false
        if (authorId != other.authorId) return false
        if (authorName != other.authorName) return false
        if (authorAvatar != other.authorAvatar) return false
        if (repostId != other.repostId) return false
        if (repostAuthorId != other.repostAuthorId) return false
        if (topicId != other.topicId) return false
        if (topicName != other.topicName) return false
        if (repostAuthorAvatar != other.repostAuthorAvatar) return false
        if (repostAuthorName != other.repostAuthorName) return false
        if (repostContent != other.repostContent) return false
        if (repostTime != other.repostTime) return false
        if (content != other.content) return false
        if (commentNum != other.commentNum) return false
        if (likeNum != other.likeNum) return false
        if (liked != other.liked) return false
        if (starred != other.starred) return false
        if (images != other.images) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + authorId.hashCode()
        result = 31 * result + authorName.hashCode()
        result = 31 * result + authorAvatar.hashCode()
        result = 31 * result + (repostId?.hashCode() ?: 0)
        result = 31 * result + (repostAuthorId?.hashCode() ?: 0)
        result = 31 * result + (topicId?.hashCode() ?: 0)
        result = 31 * result + (topicName?.hashCode() ?: 0)
        result = 31 * result + (repostAuthorAvatar?.hashCode() ?: 0)
        result = 31 * result + (repostAuthorName?.hashCode() ?: 0)
        result = 31 * result + (repostContent?.hashCode() ?: 0)
        result = 31 * result + repostTime.hashCode()
        result = 31 * result + content.hashCode()
        result = 31 * result + commentNum
        result = 31 * result + likeNum
        result = 31 * result + liked.hashCode()
        result = 31 * result + starred.hashCode()
        result = 31 * result + images.hashCode()
        return result
    }


}