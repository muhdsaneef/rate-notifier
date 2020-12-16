package com.saneef.ratenotifier.domain.model

import com.google.gson.annotations.SerializedName

class ExchangeRateApiModel(
    @SerializedName("rate") val rate: Double
)
