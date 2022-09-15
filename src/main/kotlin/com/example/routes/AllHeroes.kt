package com.example.routes

import com.example.model.ApiResponse
import com.example.repo.HeroRepo
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject
import java.lang.IllegalArgumentException
import java.lang.NumberFormatException

fun Route.getAllHeroes(){
    val heroRepo: HeroRepo by inject() //dependency injection repo implem. in get AllHeroes

    get("/naruto/heroes"){
        try {
            val page = call.request.queryParameters["page"]?.toInt() ?:1 //if doesn't receive any value, page number is 1 by default
            require(page in 1..5)  //require function throw illegal argument exception if the value is false

            val apiResponse =  heroRepo.getAllHeroes(page = page)
            call.respond(
                message = apiResponse,
                status = HttpStatusCode.OK
            )

            call.respond(message = page)
        }catch (e: NumberFormatException){
            call.respond(
                message = ApiResponse(success = false, message = "Only Numbers Allowed."),
                status = HttpStatusCode.BadRequest
            )
        }catch (e: IllegalArgumentException){
            call.respond(
                message = ApiResponse(success = false, message = "Heroes not found."),
                status = HttpStatusCode.NotFound
            )
        }
    }
}


