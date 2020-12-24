package com.saneef.ratenotifier.domain

import io.reactivex.Observable

@FunctionalInterface
interface RateNotifierRepository {

    fun fetchExchangeRate(sourceCurrency: String, targetCurrency: String): Observable<Double>
}
