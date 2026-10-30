package com.woovi.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.woovi.app.ui.navigation.WooviNavHost
import com.woovi.app.ui.theme.WooviTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WooviTheme {
                WooviNavHost()
            }
        }
    }
}