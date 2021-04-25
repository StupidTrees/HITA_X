package com.stupidtree.hita.theta.ui

import com.stupidtree.component.data.DataState


object DirtyArticles {

    private val registered = mutableMapOf<String, MutableSet<Event>>()
    private val actions = mutableMapOf<String, DataState.LIST_ACTION>()


    fun register(id: String) {
        registered[id] = mutableSetOf()
    }

    fun unregister(id: String) {
        registered.remove(id)
    }

    fun addDirtyId(id: String) {
        for (v in registered.values) {
            v.add(Event(id, EventType.REFRESH))
        }
    }

    fun addToDeleteId(id: String) {
        for (v in registered.values) {
            v.add(Event(id, EventType.DELETE))
        }
    }

    fun addDirtyId(id: String, exclude: String) {
        for ((k, v) in registered) {
            if (k != exclude) {
                v.add(Event(id, EventType.REFRESH))
            }
        }
    }

    fun addAction(action: DataState.LIST_ACTION) {
        for (k in registered.keys) {
            actions[k] = action
        }
    }

    fun getDirtyIds(name: String): List<String> {
        val result = mutableListOf<String>()
        val it = registered[name]?.iterator()
        while (it?.hasNext() == true) {
            val ev = it.next()
            if (ev.type == EventType.REFRESH) {
                result.add(ev.id)
                it.remove()
            }
        }
        return result
    }

    fun getToDeleteIds(name: String): List<String> {
        val result = mutableListOf<String>()
        val it = registered[name]?.iterator()
        while (it?.hasNext() == true) {
            val ev = it.next()
            if (ev.type == EventType.DELETE) {
                result.add(ev.id)
                it.remove()
            }
        }
        return result
    }

    fun getAction(name: String): DataState.LIST_ACTION? {
        val a = actions[name]
        actions.remove(name)
        return a
    }

    enum class EventType {
        REFRESH, DELETE
    }

    class Event(
        var id: String = "",
        var type: EventType = EventType.REFRESH
    )
}