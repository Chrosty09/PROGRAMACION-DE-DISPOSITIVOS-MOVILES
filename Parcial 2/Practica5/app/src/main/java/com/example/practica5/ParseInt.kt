package com.example.practica5

import android.app.Application
import com.parse.Parse

class ParseInt: Application(){
    override fun onCreate() {
        super.onCreate()
        Parse.initialize(
            Parse.Configuration.Builder(this)
                .applicationId("GEI87yKcYI2QUvaXMTSbDdYAbjbPTawBzjl9eoTV")
                .clientKey("tite3tACsFUzR5MT1XfeLt7cBZS5maqcKoOhQavp")
                .server("https://parseapi.back4app.com/")
                .build()
        )
    }
}