package com.stupidtree.hita.theta.ui

import com.stupidtree.component.data.DataState


object DirtyArticles {
    private val registered = mutableMapOf<String, MutableSet<String>>()
    private val actions = mutableMapOf<String, DataState.LIST_ACTION>()


    fun register(id: String) {
        registered[id] = mutableSetOf()
    }

    fun unregister(id: String) {
        registered.remove(id)
    }

    fun addDirtyId(id: String) {
        for (v in registered.values) {
            v.add(id)
        }
    }

    fun addDirtyId(id: String, exclude: String) {
        for ((k, v) in registered) {
            if (k != exclude) {
                v.add(id)
            }
        }
    }

    fun addAction(action: DataState.LIST_ACTION) {
        for (k in registered.keys) {
            actions[k] = action
        }
    }

    fun getArticleIds(name: String): List<String> {
        val r = registered[name]?.toList() ?: listOf()
        registered[name]?.clear()
        return r
    }

    fun getAction(name: String): DataState.LIST_ACTION? {
        val a = actions[name]
        actions.remove(name)
        return a
    }
}