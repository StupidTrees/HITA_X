package com.stupidtree.hita.theta.data.model

import java.sql.Timestamp

class Comment {
    var id: String = ""
    var articleId: String = ""
    var authorId: String = ""
    var authorName: String = ""
    var authorAvatar: String = ""
    var receiverId: String = ""
    var receiverName: String = ""
    var receiverAvatar: String = ""
    var replyId: String? = null
    var replyContent: String? = null
    var content: String = ""
    var likeNum: Int = 0
    var commentNum: Int = 0
    var liked: Boolean = false
    var createTime: Timestamp = Timestamp(0)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Comment

        if (id != other.id) return false
        if (authorId != other.authorId) return false
        if (authorName != other.authorName) return false
        if (authorAvatar != other.authorAvatar) return false
        if (receiverId != other.receiverId) return false
        if (receiverName != other.receiverName) return false
        if (receiverAvatar != other.receiverAvatar) return false
        if (replyId != other.replyId) return false
        if (replyContent != other.replyContent) return false
        if (content != other.content) return false
        if (likeNum != other.likeNum) return false
        if (commentNum != other.commentNum) return false
        if (liked != other.liked) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + authorId.hashCode()
        result = 31 * result + authorName.hashCode()
        result = 31 * result + authorAvatar.hashCode()
        result = 31 * result + receiverId.hashCode()
        result = 31 * result + receiverName.hashCode()
        result = 31 * result + receiverAvatar.hashCode()
        result = 31 * result + (replyId?.hashCode() ?: 0)
        result = 31 * result + (replyContent?.hashCode() ?: 0)
        result = 31 * result + content.hashCode()
        result = 31 * result + likeNum
        result = 31 * result + commentNum
        result = 31 * result + liked.hashCode()
        return result
    }

}