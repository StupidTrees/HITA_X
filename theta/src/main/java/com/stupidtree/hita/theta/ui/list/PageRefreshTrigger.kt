package com.stupidtree.hita.theta.ui.list

import com.stupidtree.component.data.DataState

class PageRefreshTrigger {
    var action:DataState.LIST_ACTION = DataState.LIST_ACTION.REPLACE_ALL
    var beforeTime: Long = 0
    var afterTime: Long = 0
    var mode: String = ""
    var pageSize: Int = 30
    var extra:String = ""
}