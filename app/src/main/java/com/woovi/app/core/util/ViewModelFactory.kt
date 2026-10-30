package com.woovi.app.core.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.woovi.app.AppContainer
import com.woovi.app.WooviApp

inline fun <reified VM : ViewModel> wooviViewModelFactory(
    crossinline create: (AppContainer) -> VM
): ViewModelProvider.Factory = viewModelFactory {
    initializer {
        val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as WooviApp
        create(app.container)
    }
}