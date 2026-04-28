package com.lumen.alarm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.lumen.alarm.domain.model.ThemeMode
import com.lumen.alarm.ui.navigation.LumenNavHost
import com.lumen.alarm.ui.navigation.Screen
import com.lumen.alarm.ui.screens.home.HomeViewModel
import com.lumen.alarm.ui.theme.LumenTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: HomeViewModel = hiltViewModel()
            val prefs by viewModel.prefs.collectAsState()
            val systemDark = isSystemInDarkTheme()

            val darkTheme = when (prefs.themeMode) {
                ThemeMode.DARK -> true
                ThemeMode.LIGHT -> false
                ThemeMode.SYSTEM -> systemDark
            }

            LumenTheme(darkTheme = darkTheme) {
                val navController = rememberNavController()
                val startDestination = if (prefs.isOnboardingComplete) {
                    Screen.Home.route
                } else {
                    Screen.Onboarding.route
                }
                LumenNavHost(
                    navController = navController,
                    startDestination = startDestination,
                )
            }
        }
    }
}
