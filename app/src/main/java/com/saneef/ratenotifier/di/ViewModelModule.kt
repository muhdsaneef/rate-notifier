package com.saneef.ratenotifier.di

import androidx.lifecycle.ViewModel
import com.saneef.ratenotifier.presentation.RateNotifierViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(RateNotifierViewModel::class)
    abstract fun bindRateNotifierViewModel(viewModel: RateNotifierViewModel): ViewModel
}
