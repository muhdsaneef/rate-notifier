package com.saneef.ratenotifier.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.saneef.ratenotifier.data.database.ConversionRateDatabase
import com.saneef.ratenotifier.data.database.RateDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object DatabaseModule {

    @Provides
    @Singleton
    fun provideConversionRateDatabase(context: Context): ConversionRateDatabase {
        return Room.databaseBuilder(
            context,
            ConversionRateDatabase::class.java,
            "conversion_rate_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideConversionRateDao(rateDatabase: ConversionRateDatabase): RateDao {
        return rateDatabase.conversionRateDao()
    }
}
