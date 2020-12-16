package com.saneef.ratenotifier.di

import android.app.Application
import android.content.Context
import com.saneef.ratenotifier.presentation.ui.RateNotifierFragment
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.DispatchingAndroidInjector
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        NetworkModule::class,
        ApiModule::class,
        AndroidInjectionModule::class,
        ViewModelModule::class,
        AppModule::class
    ]
)
interface AppComponent {

    // Factory to create instances of the AppComponent
    @Component.Factory
    interface Factory {
        // With @BindsInstance, the Context passed in will be available in the graph
        fun create(@BindsInstance context: Context): AppComponent
    }

    fun inject(rateNotifierFragment: RateNotifierFragment)
    val androidInjector: DispatchingAndroidInjector<Any>
}
