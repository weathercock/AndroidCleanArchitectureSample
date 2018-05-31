package com.kazakago.cleanarchitecture.presentation.hierarchy.about

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.kazakago.cleanarchitecture.domain.model.about.AppInfo
import com.kazakago.cleanarchitecture.domain.model.about.DeveloperInfo
import com.kazakago.cleanarchitecture.domain.usecase.about.GetAppInfoUseCase
import com.kazakago.cleanarchitecture.domain.usecase.about.GetDeveloperInfoUseCase
import com.kazakago.cleanarchitecture.presentation.R
import com.kazakago.cleanarchitecture.presentation.extension.toUri
import com.kazakago.cleanarchitecture.presentation.livedata.LiveEvent
import com.kazakago.cleanarchitecture.presentation.livedata.UnitLiveEvent
import java.util.*

class AboutViewModel(application: Application,
                     private val getAppInfoUseCase: GetAppInfoUseCase,
                     private val getDeveloperInfoUseCase: GetDeveloperInfoUseCase) : AndroidViewModel(application) {

    val finish = UnitLiveEvent()
    val versionText = MutableLiveData<String>()
    val developByText = MutableLiveData<String>()
    val copyrightText = MutableLiveData<String>()
    val openActionView = LiveEvent<Uri>()
    val openSendTo = LiveEvent<Uri>()

    private lateinit var appInfo: AppInfo
    private lateinit var developerInfo: DeveloperInfo

    init {
        getAboutInfo()
    }

    fun onClickBackIcon() {
        finish.call()
    }

    private fun getAboutInfo() {
        appInfo = getAppInfoUseCase.execute(Unit)
        developerInfo = getDeveloperInfoUseCase.execute(Unit)
        versionText.value = getApplication<Application>().getString(R.string.about_ver, appInfo.versionName)
        copyrightText.value = getApplication<Application>().getString(R.string.about_copyright, Calendar.getInstance().get(Calendar.YEAR), developerInfo.name)
    }

    fun onClickPlayStore() {
        openActionView.call(appInfo.playStoreUri.toUri())
    }

    fun onClickMail() {
        openSendTo.call(developerInfo.getMailAddressUri().toUri())
    }

    fun onClickWebSite() {
        openActionView.call(developerInfo.siteUrl.toUri())
    }

}
