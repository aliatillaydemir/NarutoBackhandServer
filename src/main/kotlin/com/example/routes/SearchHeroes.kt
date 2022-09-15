package com.example.routes

import com.example.repo.HeroRepo
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

fun Route.searchHeroes(){
    val heroRepo: HeroRepo by inject()

    get("/naruto/heroes/search"){
        val name = call.request.queryParameters["name"]

        val apiResponse = heroRepo.searchHeroes(name = name)
        call.respond(
            message = apiResponse,
            status = HttpStatusCode.OK
        )
    }



}
