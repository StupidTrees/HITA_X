package com.stupidtree.hita.theta.ui.comment

import com.stupidtree.component.data.DataState

class CommentRefreshTrigger {
    var action: DataState.LIST_ACTION = DataState.LIST_ACTION.REPLACE_ALL
    var id:String = ""
    var pageSize: Int = 30
    var pageNum:Int = 0
}