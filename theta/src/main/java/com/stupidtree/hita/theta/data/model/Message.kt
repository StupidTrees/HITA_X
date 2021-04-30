package com.stupidtree.hita.theta.data.model

import java.sql.Timestamp


class Message {
    enum class ACTION {
        LIKE, COMMENT, FOLLOW, UNFOLLOW, REPOST
    }

    enum class TYPE {
        COMMENT, ARTICLE, NONE
    }

    var id: String = ""
    var userId: String = ""
    var userName: String = ""
    var userAvatar: String = ""
    var action: ACTION = ACTION.LIKE
    var type: TYPE = TYPE.NONE
    var referenceId: String = ""
    var content: String = ""
    var image: String = ""
    var createTime: Timestamp = Timestamp(0)
    var actionContent: String = ""
}