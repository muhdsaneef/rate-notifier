package com.saneef.ratenotifier.domain.model

import com.google.gson.annotations.SerializedName

class ExchangeRateRequestModel(
    @SerializedName("guaranteedTargetAmount") val isTargetAmountGuaranteed: Boolean = false,
    @SerializedName("sourceAmount") val sourceAmount: Long = 10000,
    @SerializedName("sourceCurrency") val sourceCurrency: String,
    @SerializedName("targetCurrency") val targetCurrency: String,
    @SerializedName("preferredPayIn") val preferredPayIn: Any? = null
)
