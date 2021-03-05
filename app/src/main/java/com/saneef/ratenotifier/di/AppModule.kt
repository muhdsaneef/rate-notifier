package com.saneef.ratenotifier.di

import com.saneef.ratenotifier.data.database.RateDao
import com.saneef.ratenotifier.domain.RateNotifierApi
import com.saneef.ratenotifier.domain.RateNotifierRepository
import com.saneef.ratenotifier.domain.RateNotifierRepositoryImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    @Singleton
    fun provideRateNotifierRepository(
        rateNotifierApi: RateNotifierApi,
        rateDao: RateDao
    ): RateNotifierRepository {
        return RateNotifierRepositoryImpl(rateNotifierApi, rateDao)
    }
}
