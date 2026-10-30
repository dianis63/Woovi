package com.woovi.app
import android.app.Application

class WooviApp : Application() {
    val container: AppContainer by lazy { AppContainer() }
}