package com.saneef.ratenotifier.domain

import io.reactivex.Observable

interface RateNotifierRepository {
    fun fetchExchangeRate(): Observable<Double>
}
