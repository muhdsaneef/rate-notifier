package com.saneef.ratenotifier.presentation

import androidx.lifecycle.*
import com.saneef.ratenotifier.data.ui.ConversionRateUiModel
import com.saneef.ratenotifier.domain.RateNotifierRepository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class RateNotifierViewModel @Inject constructor(private val repository: RateNotifierRepository) :
    ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private val _exchangeRateState = MutableLiveData<ConversionRateUiModel>()
    val exchangeRateViewState: LiveData<ConversionRateUiModel>
        get() = _exchangeRateState

    private val _uiState = MutableLiveData<UiState>()
    val uiViewState: LiveData<UiState>
        get() = _uiState

    val storedExchangeRates = repository.fetchStoredExchangeRates().asLiveData()

    var sourceCurrency = MutableLiveData<String>()
    var targetCurrency = MutableLiveData<String>()

    @ExperimentalCoroutinesApi
    private val newValueSignal = BroadcastChannel<AnimationAction>(Channel.BUFFERED)
    @ExperimentalCoroutinesApi
    val newValueSignalReceiver = newValueSignal.asFlow()

    fun loadExchangeRate() {
        compositeDisposable.add(
            getLoadExchangeRateObservable()
                .doOnSubscribe { _uiState.postValue(UiState.LOADING) }
                .subscribe(this::handleSuccess, this::handleFailure)
        )
    }

    private fun getLoadExchangeRateObservable(): Observable<ConversionRateUiModel>{
        return repository.fetchExchangeRate(
            sourceCurrency.value.orEmpty(),
            targetCurrency.value.orEmpty()
        ).observeOn(AndroidSchedulers.mainThread())
    }

    private fun handleSuccess(rate: ConversionRateUiModel) {
        _exchangeRateState.postValue(rate)
        _uiState.postValue(UiState.SUCCESS)
    }

    private fun handleAutoUpdate(conversionRateUiModel: ConversionRateUiModel) {
        _exchangeRateState.postValue(conversionRateUiModel)
    }

    private fun handleFailure(throwable: Throwable) {
        Timber.d("Failed to load rate - $throwable")
        _uiState.postValue(UiState.ERROR)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    @ExperimentalCoroutinesApi
    fun onAutoUpdateCheckChanged(checked: Boolean) {
        sendAnimationAction(if (checked) AnimationAction.Start else AnimationAction.Stop)
        if (!checked) {
            compositeDisposable.clear()
        } else {
            compositeDisposable.add(
                Observable.interval(0L, 3L, TimeUnit.SECONDS, Schedulers.io())
                    .subscribe {
                    getLoadExchangeRateObservable().subscribe(
                        this::handleAutoUpdate,
                        this::handleFailure
                    )
                })
        }
    }

    @ExperimentalCoroutinesApi
    private fun sendAnimationAction(action: AnimationAction) {
        viewModelScope.launch {
            newValueSignal.send(action)
        }
    }
}

sealed class AnimationAction {
    object Start: AnimationAction()
    object Stop: AnimationAction()
}

enum class UiState {
    SUCCESS,
    LOADING,
    ERROR
}
