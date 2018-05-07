package com.kazakago.cleanarchitecture.domain.usecase.about

import com.kazakago.cleanarchitecture.domain.model.about.AppInfo
import com.kazakago.cleanarchitecture.domain.repository.about.AboutRepository

class GetAppInfoUseCaseImpl(private val aboutRepository: AboutRepository) : GetAppInfoUseCase {

    override fun execute(input: Unit): AppInfo {
        return aboutRepository.getAppInfo()
    }

}