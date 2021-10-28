package com.stupidtree.hita.theta.ui.topic

import com.stupidtree.component.data.DataState

class PageRefreshTrigger {
    var action: DataState.LIST_ACTION = DataState.LIST_ACTION.REPLACE_ALL
    var mode: String = ""
    var pageSize: Int = 30
    var pageNum: Int = 0
    var extra: String = ""
}