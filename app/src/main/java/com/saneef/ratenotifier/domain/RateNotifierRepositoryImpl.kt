package com.saneef.ratenotifier.domain

import com.saneef.ratenotifier.data.database.RateDao
import com.saneef.ratenotifier.data.database.model.ConversionRate
import com.saneef.ratenotifier.data.ui.ConversionRateUiModel
import com.saneef.ratenotifier.domain.model.ExchangeRateApiModel
import com.saneef.ratenotifier.domain.model.ExchangeRateRequestModel
import com.saneef.ratenotifier.utils.DateUtils
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject

class RateNotifierRepositoryImpl @Inject constructor(
    private val rateNotifierApi: RateNotifierApi,
    private val rateDao: RateDao
) : RateNotifierRepository {

    override fun fetchExchangeRate(
        sourceCurrency: String,
        targetCurrency: String
    ): Observable<ConversionRateUiModel> {
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
            .map {
                storeInDatabase(it)
                ConversionRateUiModel(DateUtils.formatDateTime(System.currentTimeMillis()), it.rate)
            }
    }

    private fun storeInDatabase(apiModel: ExchangeRateApiModel) {
        val timeStamp = System.currentTimeMillis()
        rateDao.insertConversionRate(ConversionRate(rate = apiModel.rate, timestamp = timeStamp))
    }

    override fun fetchStoredExchangeRates(): Flow<List<ConversionRateUiModel>> {
        return rateDao.fetchAllRates().map { conversionRates ->
            conversionRates.map {
                ConversionRateUiModel(
                    DateUtils.formatDateTime(it.timestamp), it.rate
                )
            }
        }
    }
}
