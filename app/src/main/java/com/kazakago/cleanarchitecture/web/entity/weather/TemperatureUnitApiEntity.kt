package com.kazakago.cleanarchitecture.web.entity.weather

/**
 * Temperature Unit API Entity
 *
 * Created by tamura_k on 2016/06/03.
 */
data class TemperatureUnitApiEntity(
        //摂氏
        var celsius: Float? = null,
        //華氏
        var fahrenheit: Float? = null
)