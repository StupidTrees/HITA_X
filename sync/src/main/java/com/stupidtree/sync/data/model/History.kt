package com.stupidtree.sync.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson


@Entity(tableName = "history")
class History {

    enum class ACTION { REMOVE, REQUIRE,CLEAR }

    @PrimaryKey
    var id: Long = 0
    var uid: String = ""
    var table: String = ""
    var action: ACTION = ACTION.REQUIRE
    var ids: List<String> = mutableListOf()

    override fun toString(): String {
        return "History(id=$id, uid='$uid', table='$table', action=$action, ids=$ids)"
    }

    fun getAvatar(): Avatar {
        val res = Avatar()
        res.id = id
        res.action = action
        res.uid = uid
        res.table = table
        res.ids = Gson().toJson(ids)
        return res
    }

    class Avatar {
        var id: Long = 0
        var uid: String = ""
        var table: String = ""
        var action: ACTION = ACTION.REQUIRE
        var ids: String = ""
    }


}