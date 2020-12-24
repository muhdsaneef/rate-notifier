package com.saneef.ratenotifier.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.saneef.ratenotifier.domain.RateNotifierRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import javax.inject.Inject

class RateNotifierViewModel @Inject constructor(private val repository: RateNotifierRepository) :
    ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private val _exchangeRateState = MutableLiveData<Double>()
    val exchangeRateViewState: LiveData<Double>
        get() = _exchangeRateState

    private val _uiState = MutableLiveData<UiState>()
    val uiViewState: LiveData<UiState>
        get() = _uiState

    var sourceCurrency = MutableLiveData<String>()
    var targetCurrency = MutableLiveData<String>()
    val _amount = MutableLiveData<String>()
    val amountViewState: LiveData<String>
    get() = _amount


    fun convertAmount(amount: Double) {
        if (amount !=0.0 && _exchangeRateState.value != null) {
           _amount.postValue((amount*_exchangeRateState.value!!).toString())
        }
    }

    fun loadExchangeRate() {
        compositeDisposable.add(
            repository.fetchExchangeRate(
                sourceCurrency.value.orEmpty(),
                targetCurrency.value.orEmpty()
            )
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { _uiState.postValue(UiState.LOADING) }
                .subscribe(this::handleSuccess, this::handleFailure)
        )
    }

    private fun handleSuccess(rate: Double) {
        _exchangeRateState.postValue(rate)
        _uiState.postValue(UiState.SUCCESS)
    }

    private fun handleFailure(throwable: Throwable) {
        Timber.d("Failed to load rate - $throwable")
        _uiState.postValue(UiState.ERROR)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}

enum class UiState {
    SUCCESS,
    LOADING,
    ERROR
}
