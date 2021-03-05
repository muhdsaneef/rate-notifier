package com.saneef.ratenotifier.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.saneef.ratenotifier.data.database.model.ConversionRate

@Database(entities = [ConversionRate::class], version = 1)
abstract class ConversionRateDatabase: RoomDatabase() {

    abstract fun conversionRateDao(): RateDao
}
