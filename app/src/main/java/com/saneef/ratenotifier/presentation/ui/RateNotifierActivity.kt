package com.saneef.ratenotifier.presentation.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.saneef.ratenotifier.R

class RateNotifierActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.rate_notifier_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, RateNotifierFragment.newInstance())
                .commitNow()
        }
    }
}
