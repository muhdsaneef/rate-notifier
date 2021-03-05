package com.saneef.ratenotifier.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.saneef.ratenotifier.data.database.model.ConversionRate
import kotlinx.coroutines.flow.Flow

@Dao
interface RateDao {


    @Query("select * from RateTable")
    fun fetchAllRates(): Flow<List<ConversionRate>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertConversionRate(conversionRate: ConversionRate)
}
