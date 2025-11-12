package com.example.livequery

import com.parse.ParseClassName
import com.parse.ParseObject

@ParseClassName("Mensajes")
class Mensaje : ParseObject() {
    var texto: String?
        get() = getString("texto")
        set(value) = put("texto", value ?: "")
}

