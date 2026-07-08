package com.phywarp.time

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
