package com.stupidtree.hita.data.model.eas

import java.util.*

class EASToken {

    var cookies = HashMap<String, String>()
        get() = field
        set(value) {
            field.clear()
            field.putAll(value)
        }


}