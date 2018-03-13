package com.kazakago.cleanarchitecture.domain.usecase.weather

import com.kazakago.cleanarchitecture.domain.model.city.CityId
import com.kazakago.cleanarchitecture.domain.model.weather.Weather
import com.kazakago.cleanarchitecture.domain.repository.weather.WeatherApiRepository
import com.kazakago.cleanarchitecture.domain.repository.weather.WeatherRepository
import io.reactivex.Single

class GetWeatherUseCaseImpl(private val weatherApiRepository: WeatherApiRepository, private val weatherRepository: WeatherRepository) : GetWeatherUseCase {

    override fun execute(input: CityId): Single<Weather> {
        return weatherApiRepository.fetch(input)
                .doOnSuccess {
                    it.cityId = input
                    weatherRepository.find(input)?.let {
                        weatherRepository.delete(it)
                    }
                    weatherRepository.insert(it)
                }
                .onErrorReturn {
                    weatherRepository.find(input) ?: throw it
                }
    }

}