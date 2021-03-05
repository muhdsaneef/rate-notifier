package com.saneef.ratenotifier.domain

import com.saneef.ratenotifier.data.ui.ConversionRateUiModel
import io.reactivex.Observable
import kotlinx.coroutines.flow.Flow

@FunctionalInterface
interface RateNotifierRepository {

    fun fetchExchangeRate(sourceCurrency: String, targetCurrency: String): Observable<ConversionRateUiModel>

    fun fetchStoredExchangeRates(): Flow<List<ConversionRateUiModel>>
}
