package com.kazakago.cleanarchitecture.domain.usecase.weather

import com.kazakago.cleanarchitecture.domain.model.city.CityId
import com.kazakago.cleanarchitecture.domain.model.weather.Weather
import com.kazakago.cleanarchitecture.domain.usecase.SuspendingUseCase

interface GetWeatherUseCase : SuspendingUseCase<CityId, Weather>