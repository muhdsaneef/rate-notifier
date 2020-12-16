package com.saneef.ratenotifier.di

import com.saneef.ratenotifier.domain.RateNotifierApi
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
class ApiModule {

    @Provides
    @Singleton
    fun provideRateNotifierApiService(retrofit: Retrofit): RateNotifierApi {
        return retrofit.create(RateNotifierApi::class.java)
    }
}
