package com.saneef.ratenotifier.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "RateTable")
data class ConversionRate(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "rate") val rate: Double,
    @ColumnInfo(name = "timestamp") val timestamp: Long
)
