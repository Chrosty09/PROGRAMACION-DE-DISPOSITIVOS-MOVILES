package com.example.livequery

import android.app.Application
import com.parse.Parse
import com.parse.ParseObject
import com.parse.livequery.ParseLiveQueryClient
import java.net.URI

class ParseInt: Application(){
    override fun onCreate() {
        super.onCreate()
        ParseObject.registerSubclass(Mensaje::class.java)

        Parse.initialize(
            Parse.Configuration.Builder(this)
                .applicationId("ocewe7lAehoWyPMZbupxuBdYpiVPouQ3aM9druBD")
                .clientKey("PwE0aOx1Oplfqmgq2EPMP5yRR0EjTuNCb6N4aukB")
                .server("https://parseapi.back4app.com/")
                .build()
        )
        val parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient(
            URI("wss://livequery.back4app.com")
        )
    }
}