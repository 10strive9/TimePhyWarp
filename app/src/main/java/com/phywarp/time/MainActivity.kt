package com.phywarp.time

import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.phywarp.time.data.local.UserPreferences
import com.phywarp.time.ui.TimerViewModel
import com.phywarp.time.ui.screens.MainScreen
import com.phywarp.time.ui.theme.AppTheme
import com.phywarp.time.ui.theme.TimePhyWarpTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Hide status bar and navigation bar
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        
        val userPreferences = UserPreferences(this)
        
        setContent {
            val themeColorName by userPreferences.themeColor.collectAsState(initial = "BLUE")
            val appTheme = try { AppTheme.valueOf(themeColorName) } catch (e: Exception) { AppTheme.BLUE }
            
            TimePhyWarpTheme(appTheme = appTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: TimerViewModel = viewModel()
                    MainScreen(viewModel, userPreferences)
                }
            }
        }
    }
}
