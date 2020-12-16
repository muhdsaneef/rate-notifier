package com.saneef.ratenotifier.app

import android.app.Application
import com.saneef.ratenotifier.BuildConfig
import com.saneef.ratenotifier.di.AppComponent
import com.saneef.ratenotifier.di.DaggerAppComponent
import timber.log.Timber

class RateNotifierApplication : Application() {

    val appComponent: AppComponent by lazy {
        initializeComponent()
    }

    private fun initializeComponent(): AppComponent {
        // Creates an instance of AppComponent using its Factory constructor
        // We pass the applicationContext that will be used as Context in the graph
        return DaggerAppComponent.factory().create(applicationContext)
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
