package com.saneef.ratenotifier.domain

import com.saneef.ratenotifier.domain.model.ExchangeRateApiModel
import com.saneef.ratenotifier.domain.model.ExchangeRateRequestModel
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

interface RateNotifierApi {

    @POST("quotes/")
    fun fetchExchangeRate(
        @Body exchangeRateRequest: ExchangeRateRequestModel
    ): Observable<ExchangeRateApiModel>
}
