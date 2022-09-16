package com.example.model

import kotlinx.serialization.Serializable

@Serializable  //thank to this annotation we able to send object to other class or anyway... (so, we can convert this to JSON response)
data class ApiResponse(  //it's contains hero data class. this class is responsible from api (requests)responses.
    val success: Boolean,  //it is return false or true. it is required for api.
    val message: String? = null, //this and other func. are optional.
    val prevPage: Int? = null,   // prev and next page added cause android apps will support  page library. We can use these.
    val nextPage: Int? = null,
    val heroes: List<Hero> = emptyList(),  //hero data class
    val lastUpdated: Long? = null
)
