package com.kazakago.cleanarchitecture.domain.usecase.appInfo

import com.kazakago.cleanarchitecture.domain.repository.AppInfoRepository

class GetAppVersionUseCaseImpl(private val appInfoRepository: AppInfoRepository) : GetAppVersionUseCase {

    override fun execute(input: Unit): String = appInfoRepository.appVersion

}