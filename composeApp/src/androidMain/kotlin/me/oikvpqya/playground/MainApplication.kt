package me.oikvpqya.playground

import android.app.Application
import di.AndroidApplicationComponent
import di.ApplicationComponentProvider
import di.create

class MainApplication : Application(), ApplicationComponentProvider {

    override val component by lazy(LazyThreadSafetyMode.NONE) {
        AndroidApplicationComponent::class.create(this)
    }
}
