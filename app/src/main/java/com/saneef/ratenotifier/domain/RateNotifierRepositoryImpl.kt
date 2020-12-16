package com.saneef.ratenotifier.domain

import com.saneef.ratenotifier.domain.model.ExchangeRateRequestModel
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class RateNotifierRepositoryImpl @Inject constructor(private val rateNotifierApi: RateNotifierApi) :
    RateNotifierRepository {
    override fun fetchExchangeRate(): Observable<Double> {
        val request = ExchangeRateRequestModel(sourceCurrency = "SGD", targetCurrency = "INR")

        return rateNotifierApi.fetchExchangeRate(request).subscribeOn(Schedulers.io())
            .map { it.rate }

    }
}
