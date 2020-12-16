package com.saneef.ratenotifier.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.saneef.ratenotifier.domain.RateNotifierRepository
import com.saneef.ratenotifier.domain.RateNotifierRepositoryImpl
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import java.util.concurrent.TimeUnit
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

    fun loadExchangeRate() {
        compositeDisposable.add(
            repository.fetchExchangeRate()
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
