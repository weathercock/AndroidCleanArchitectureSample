package com.weathercock.cleanarchitecture.di.module;

import android.content.Context;

import com.weathercock.cleanarchitecture.CleanApplication;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {

    private CleanApplication cleanApplication;

    public ApplicationModule(CleanApplication cleanApplication) {
        this.cleanApplication = cleanApplication;
    }

    @Provides
    @Singleton
    public Context provideApplicationContext() {
        return cleanApplication.getApplicationContext();
    }

}