package com.example

import com.example.model.ApiResponse
import com.example.repo.HeroRepo
import com.example.repo.NEXT_PAGE_KEY
import com.example.repo.PREVIOUS_PAGE_KEY
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.koin.java.KoinJavaComponent.inject
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {

    private val heroRepo: HeroRepo by inject(HeroRepo::class.java)

    @Test
    fun `access root endpoint, assert correct information`() {
        withTestApplication(moduleFunction = Application::module) {
            handleRequest(HttpMethod.Get, "/").apply {
                assertEquals(
                    expected = HttpStatusCode.OK,
                    actual = response.status()
                )
                assertEquals(expected = "Welcome to Naruto API",
                    actual = response.content
                )
            }
        }
    }

    @ExperimentalSerializationApi
    @Test
    fun `access all heroes endpoint, query all pages, assert correct information`(){
        withTestApplication(moduleFunction = Application::module) {
            val pages = 1..5
            val heroes = listOf(
                heroRepo.page1,
                heroRepo.page2,
                heroRepo.page3,
                heroRepo.page4,
                heroRepo.page5
                )
            pages.forEach{page ->
                handleRequest(HttpMethod.Get, "/naruto/heroes?page=$page").apply{
                    assertEquals(
                        expected = HttpStatusCode.OK,
                        actual = response.status()
                    )
                    val actual = Json.decodeFromString<ApiResponse>(response.content.toString())
                    val expected = ApiResponse(
                        success = true,
                        message = "ok",
                        prevPage = calculatePage(page = page)["prevPage"],
                        nextPage = calculatePage(page = page)["nextPage"],
                        heroes = heroes[page-1],
                        lastUpdated = actual.lastUpdated
                    )

                    assertEquals(
                        expected = expected,
                        actual= actual
                    )
                }

            }
        }
    }

    private fun calculatePage(page:Int) =
        mapOf(
            PREVIOUS_PAGE_KEY to if(page in 2..5) page.minus(1) else null,
            NEXT_PAGE_KEY to if(page in 1..4) page.plus(1) else null
        )

    @ExperimentalSerializationApi
    @Test
    fun `access all heroes endpoint, query non existing page number, assert error`() {
        withTestApplication(moduleFunction = Application::module) {
            handleRequest(HttpMethod.Get, "/naruto/heroes?page=6").apply {
                assertEquals(
                    expected = HttpStatusCode.NotFound,
                    actual = response.status()
                )
                val expected = ApiResponse(
                    success = false,
                    message = "Heroes not found."
                )

                val  actual = Json.decodeFromString<ApiResponse>(response.content.toString())
                println("Expected: $expected")
                println("Actual: $actual")

                assertEquals(
                    expected = expected,
                    actual = actual
                )

            }
        }
    }


    @ExperimentalSerializationApi
    @Test
    fun `access all heroes endpoint, query invalid page number, assert error`() {
        withTestApplication(moduleFunction = Application::module) {
            handleRequest(HttpMethod.Get, "/naruto/heroes?page=invalid").apply {
                assertEquals(
                    expected = HttpStatusCode.BadRequest,
                    actual = response.status()
                )
                val expected = ApiResponse(
                    success = false,
                    message = "Only Numbers Allowed."
                )

                val  actual = Json.decodeFromString<ApiResponse>(response.content.toString())
                println("Expected: $expected")
                println("Actual: $actual")

                assertEquals(
                    expected = expected,
                    actual = actual
                )

            }
        }
    }


    @ExperimentalSerializationApi
    @Test
    fun `access search heroes endpoint, query hero name, assert single hero result`() {
        withTestApplication(moduleFunction = Application::module) {
            handleRequest(HttpMethod.Get, "/naruto/heroes/search?name=sas").apply {
                assertEquals(
                    expected = HttpStatusCode.OK,
                    actual = response.status()
                )

                val  actual = Json.decodeFromString<ApiResponse>(response.content.toString())
                    .heroes.size

                assertEquals(
                    expected = 1,
                    actual = actual
                )

            }
        }
    }


    @ExperimentalSerializationApi
    @Test
    fun `access search heroes endpoint, query hero name, assert multiple hero result`() {
        withTestApplication(moduleFunction = Application::module) {
            handleRequest(HttpMethod.Get, "/naruto/heroes/search?name=sa").apply {
                assertEquals(
                    expected = HttpStatusCode.OK,
                    actual = response.status()
                )

                val  actual = Json.decodeFromString<ApiResponse>(response.content.toString())
                    .heroes.size

                assertEquals(
                    expected = 3,
                    actual = actual
                )

            }
        }
    }


    @ExperimentalSerializationApi
    @Test
    fun `access search heroes endpoint, query an empty text, assert empty list as a result`() {
        withTestApplication(moduleFunction = Application::module) {
            handleRequest(HttpMethod.Get, "/naruto/heroes/search?name=").apply {
                assertEquals(
                    expected = HttpStatusCode.OK,
                    actual = response.status()
                )

                val  actual = Json.decodeFromString<ApiResponse>(response.content.toString())
                    .heroes

                assertEquals(
                    expected = emptyList(),
                    actual = actual
                )

            }
        }
    }


    @ExperimentalSerializationApi
    @Test
    fun `access search heroes endpoint, query non existing hero, assert empty list as a result`() {
        withTestApplication(moduleFunction = Application::module) {
            handleRequest(HttpMethod.Get, "/naruto/heroes/search?name=ssdasdasdasfda").apply {
                assertEquals(
                    expected = HttpStatusCode.OK,
                    actual = response.status()
                )

                val  actual = Json.decodeFromString<ApiResponse>(response.content.toString())
                    .heroes

                assertEquals(
                    expected = emptyList(),
                    actual = actual
                )

            }
        }
    }


    @ExperimentalSerializationApi
    @Test
    fun `access non existing endpoint, not found`() {
        withTestApplication(moduleFunction = Application::module) {
            handleRequest(HttpMethod.Get, "/unknown").apply {
                assertEquals(
                    expected = HttpStatusCode.NotFound,
                    actual = response.status()
                )
                assertEquals(
                    expected = "Page not Found",
                    actual = response.content
                )

            }
        }
    }


}