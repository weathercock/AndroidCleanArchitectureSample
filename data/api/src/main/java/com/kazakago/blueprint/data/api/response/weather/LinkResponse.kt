package com.kazakago.blueprint.data.api.response.weather

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LinkResponse(
    @Json(name = "name")
    val name: String,
    @Json(name = "link")
    val link: String
)