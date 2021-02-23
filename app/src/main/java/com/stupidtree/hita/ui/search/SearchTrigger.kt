package com.stupidtree.hita.ui.search


import com.stupidtree.hita.ui.base.Trigger

class SearchTrigger : Trigger() {

    var text: String = ""
    var pageSize: Int = 0
    var pageNum: Int = 0
    var append: Boolean = false

    companion object {
        fun getActioning(
            text: String,
            pageSize: Int,
            pageNum: Int,
            append: Boolean
        ): SearchTrigger {
            val r = SearchTrigger()
            r.append = append
            r.pageNum = pageNum
            r.pageSize = pageSize
            r.text = text
            r.setActioning()
            return r
        }
    }
}