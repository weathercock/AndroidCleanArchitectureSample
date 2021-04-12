package com.kazakago.blueprint.data.api.hierarchy.weather

import com.kazakago.blueprint.data.api.response.weather.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

internal interface WeatherService {

    @GET("json/v1")
    suspend fun fetch(@Query("city") cityId: String): Response<WeatherResponse>
}