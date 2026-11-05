package com.example.proyectolince.data.network

import dev.insforge.auth.Auth
import dev.insforge.createInsforgeClient


object InsforgeProvider {

    val cliente = createInsforgeClient(
        baseURL = "https://w74m5jn3.us-east.insforge.app/",
        anonKey = "ik_5fe17ad90fedb881038bec3b7811a31d"
    ) {

        install(Auth)
    }
}