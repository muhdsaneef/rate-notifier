package com.saneef.ratenotifier.domain

import com.saneef.ratenotifier.domain.model.ExchangeRateRequestModel
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class RateNotifierRepositoryImpl @Inject constructor(private val rateNotifierApi: RateNotifierApi) :
    RateNotifierRepository {
    override fun fetchExchangeRate(
        sourceCurrency: String,
        targetCurrency: String
    ): Observable<Double> {
        val source = if (sourceCurrency.isNotEmpty()) {
            sourceCurrency
        } else {
            "SGD"
        }
        val target = if (targetCurrency.isNotEmpty()) {
            targetCurrency
        } else {
           "INR"
        }
        val request = ExchangeRateRequestModel(sourceCurrency = source, targetCurrency = target)

        return rateNotifierApi.fetchExchangeRate(request).subscribeOn(Schedulers.io())
            .map { it.rate }

    }
}
